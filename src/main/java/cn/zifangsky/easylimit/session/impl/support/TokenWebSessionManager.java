package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.token.ExpiredTokenException;
import cn.zifangsky.easylimit.exception.token.InvalidTokenException;
import cn.zifangsky.easylimit.exception.token.TokenException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.TokenDAO;
import cn.zifangsky.easylimit.session.TokenOperateResolver;
import cn.zifangsky.easylimit.session.impl.DefaultTokenOperateResolver;
import cn.zifangsky.easylimit.session.impl.MemoryTokenDAO;
import cn.zifangsky.easylimit.session.impl.TokenSessionContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;

/**
 * 基于token模式的session管理器
 *
 * @author zifangsky
 * @date 2019/6/3
 * @since 1.0.0
 */
public class TokenWebSessionManager extends CookieWebSessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenWebSessionManager.class);

    /**
     * 创建Token的最大尝试次数
     */
    public static final int MAX_TOKEN_RETRIES_TIMES = 3;

    /**
     * token的基本参数
     */
    private TokenInfo tokenInfo;

    /**
     * token的基本操作
     */
    private TokenOperateResolver tokenOperateResolver;

    /**
     * Access Token和Refresh Token的存储
     */
    private TokenDAO tokenDAO;

    /**
     * 创建Token的尝试次数，默认为：{@link #MAX_TOKEN_RETRIES_TIMES}
     */
    private int tokenRetriesTimes;

    public TokenWebSessionManager() {
        this(new TokenInfo(), new DefaultTokenOperateResolver(), new MemoryTokenDAO());
    }

    public TokenWebSessionManager(TokenInfo tokenInfo) {
        this(tokenInfo, new DefaultTokenOperateResolver(), new MemoryTokenDAO());
    }

    public TokenWebSessionManager(TokenOperateResolver tokenOperateResolver, TokenDAO tokenDAO) {
        this(new TokenInfo(), tokenOperateResolver, tokenDAO);
    }

    public TokenWebSessionManager(TokenInfo tokenInfo, TokenOperateResolver tokenOperateResolver, TokenDAO tokenDAO) {
        //默认token模式不再使用cookie
        super(false);

        if(tokenInfo == null){
            throw new IllegalArgumentException("Parameter tokenInfo cannot be empty.");
        }
        if(tokenOperateResolver == null){
            throw new IllegalArgumentException("Parameter tokenOperateResolver cannot be empty.");
        }
        if(tokenDAO == null){
            throw new IllegalArgumentException("Parameter tokenDAO cannot be empty.");
        }

        this.tokenInfo = tokenInfo;
        this.tokenOperateResolver = tokenOperateResolver;
        this.tokenDAO = tokenDAO;
        //默认创建Token时最多尝试3次
        this.tokenRetriesTimes = MAX_TOKEN_RETRIES_TIMES;

        super.setGlobalTimeout(tokenInfo.getAccessTokenTimeout());
        super.setGlobalTimeoutChronoUnit(tokenInfo.getAccessTokenTimeoutUnit());
    }

    /**
     * 创建Access Token
     * @author zifangsky
     * @date 2019/6/4 17:45
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param session 当前会话
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    public SimpleAccessToken createAccessToken(PrincipalInfo principalInfo, Session session){
        SimpleAccessToken accessToken = this.doCreateAccessToken(principalInfo, this.tokenInfo, session.getId());
        this.onCreateAccessToken(principalInfo, accessToken);

        return accessToken;
    }

    /**
     * 创建Refresh Token
     * @author zifangsky
     * @date 2019/6/4 17:51
     * @since 1.0.0
     * @param validatedInfo 登录信息
     * @param accessToken Access Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken
     */
    public SimpleRefreshToken createRefreshToken(ValidatedInfo validatedInfo, SimpleAccessToken accessToken){
        SimpleRefreshToken refreshToken = this.doCreateRefreshToken(validatedInfo, this.tokenInfo, accessToken.getAccessToken());
        this.onCreateRefreshToken(accessToken, refreshToken);

        return refreshToken;
    }

    /**
     * 获取Refresh Token对象
     * @author zifangsky
     * @date 2019/6/13 14:16
     * @since 1.0.0
     * @param refreshToken Refresh Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken
     */
    public SimpleRefreshToken getRefreshToken(String refreshToken) throws TokenException{
        //1. 获取SimpleRefreshToken
        SimpleRefreshToken simpleRefreshToken = this.tokenDAO.readByRefreshToken(refreshToken);

        //2. 校验SimpleRefreshToken是否为空
        if(simpleRefreshToken == null){
            String msg = MessageFormat.format("SimpleRefreshToken cannot be retrieved with refreshToken[{0}].", refreshToken);
            LOGGER.error(msg);
            throw new InvalidTokenException(msg);
        }

        //3. 校验SimpleRefreshToken是否有效
        if(!this.tokenOperateResolver.isValid(simpleRefreshToken)){
            //移除过期Refresh Token
            this.removeExpiredRefreshToken(simpleRefreshToken);

            String msg = MessageFormat.format("refreshToken with id [{0}] has expired.", refreshToken);
            LOGGER.error(msg);
            throw new ExpiredTokenException(msg);
        }

        return simpleRefreshToken;
    }

    /**
     * 刷新Access Token
     * @author zifangsky
     * @date 2019/6/13 14:36
     * @since 1.0.0
     * @param simpleRefreshToken Refresh Token
     * @param principalInfo 用户主体信息
     * @param session session
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessRefreshToken
     */
    public SimpleAccessRefreshToken refreshAccessToken(SimpleRefreshToken simpleRefreshToken, PrincipalInfo principalInfo, Session session) {
        //1. 生成新的Access Token
        SimpleAccessToken newAccessToken = this.doCreateAccessToken(principalInfo, this.tokenInfo, session.getId());

        //2. 移除失效的Access Token
        this.removeInvalidAccessToken(simpleRefreshToken.getAccessToken());

        //3. 更新Access Token和Refresh Token
        simpleRefreshToken.setAccessToken(newAccessToken.getAccessToken());
        this.tokenDAO.updateAccessToken(newAccessToken);
        this.tokenDAO.updateRefreshToken(simpleRefreshToken);

        //4. 将之保存到session
        session.setAttribute(TokenSessionContext.SIMPLE_ACCESS_TOKEN_KEY, newAccessToken);
        session.setAttribute(TokenSessionContext.SIMPLE_REFRESH_TOKEN_KEY, simpleRefreshToken);

        //5. 返回Access Token和Refresh Token
        return new SimpleAccessRefreshToken(newAccessToken, simpleRefreshToken);
    }

    @Override
    protected void doStopped(Session session) {
        this.removeExpiredAccessToken(session);
    }

    @Override
    protected void doExpired(Session session) {
        this.removeExpiredAccessToken(session);
    }

    /**
     * 重写SessionId的获取方式
     */
    @Override
    public Serializable getSessionId(ServletRequest request, ServletResponse response) throws TokenException{
        if(request != null && request instanceof HttpServletRequest){
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            Serializable sessionId = null;
            if(this.isEnableSessionIdCookie()){
                sessionId =  this.getCookieSessionId(httpServletRequest);
            }

            //如果cookie中没有，则尝试从请求中获取Access Token
            if(sessionId == null){
                String accessToken = this.getAccessTokenFromRequest(httpServletRequest);

                if(accessToken != null){
                    SimpleAccessToken simpleAccessToken = this.tokenDAO.readByAccessToken(accessToken);
                    if(simpleAccessToken == null){
                        String msg = MessageFormat.format("SimpleAccessToken cannot be retrieved with accessToken[{0}].", accessToken);
                        LOGGER.error(msg);
                        throw new InvalidTokenException(msg);
                    }else{
                        //如果Access Token已经过期，则抛出异常
                        if(!this.tokenOperateResolver.isValid(simpleAccessToken)){
                            //移除过期Access Token
                            this.removeExpiredAccessToken(simpleAccessToken);

                            String msg = MessageFormat.format("accessToken with id [{0}] has expired.", accessToken);
                            LOGGER.error(msg);
                            throw new ExpiredTokenException(msg);
                        }

                        sessionId = simpleAccessToken.getSessionId();
                    }
                }
            }

            return sessionId;
        }

        return null;
    }

    /**
     * 创建Access Token并判断是否跟已有的重复
     */
    protected SimpleAccessToken doCreateAccessToken(PrincipalInfo principalInfo, TokenInfo tokenInfo, Serializable sessionId){
        if(this.tokenRetriesTimes <= 0){
            throw new IllegalArgumentException("Parameter tokenRetriesTimes cannot be less than or equal to zero.");
        }

        SimpleAccessToken newSimpleAccessToken = null;
        for(int i = 0; i < tokenRetriesTimes; i++){
            //1. 创建Access Token
            newSimpleAccessToken = this.tokenOperateResolver.createAccessToken(principalInfo, tokenInfo, sessionId);

            //2. 判断是否跟已有Access Token重复
            if(newSimpleAccessToken != null && newSimpleAccessToken.getAccessToken() != null){
                SimpleAccessToken savedSimpleAccessToken = this.tokenDAO.readByAccessToken(newSimpleAccessToken.getAccessToken());
                if(savedSimpleAccessToken == null){
                    return newSimpleAccessToken;
                }
            }
        }

        throw new TokenException(MessageFormat.format("The available access_token cannot be created within {0} times.", tokenRetriesTimes));
    }

    /**
     * 创建Refresh Token并判断是否跟已有的重复
     */
    protected SimpleRefreshToken doCreateRefreshToken(ValidatedInfo validatedInfo, TokenInfo tokenInfo, String accessToken){
        if(this.tokenRetriesTimes <= 0){
            throw new IllegalArgumentException("Parameter tokenRetriesTimes cannot be less than or equal to zero.");
        }

        SimpleRefreshToken newSimpleRefreshToken = null;
        for(int i = 0; i < tokenRetriesTimes; i++){
            //1. 创建Refresh Token
            newSimpleRefreshToken = this.tokenOperateResolver.createRefreshToken(validatedInfo, tokenInfo, accessToken);

            //2. 判断是否跟已有Refresh Token重复
            if(newSimpleRefreshToken != null && newSimpleRefreshToken.getRefreshToken() != null){
                SimpleRefreshToken savedSimpleRefreshToken = this.tokenDAO.readByRefreshToken(newSimpleRefreshToken.getRefreshToken());
                if(savedSimpleRefreshToken == null){
                    return newSimpleRefreshToken;
                }
            }
        }

        throw new TokenException(MessageFormat.format("The available refresh_token cannot be created within {0} times.", tokenRetriesTimes));
    }

    /**
     * 从请求中获取Access Token
     * @author zifangsky
     * @date 2019/6/3 15:39
     * @since 1.0.0
     * @param request request
     * @return java.lang.String
     */
    protected String getAccessTokenFromRequest(HttpServletRequest request){
        //1. 先从请求参数中获取
        String accessToken = request.getParameter(tokenInfo.getAccessTokenParamName());

        if(accessToken == null){
            //2. 如果参数中不存在，则尝试从Header中获取
            accessToken = request.getHeader(tokenInfo.getAccessTokenParamName());

            if(accessToken == null){
                String authorization = request.getHeader("Authorization");
                if(StringUtils.isNoneBlank(authorization)){
                    accessToken = authorization;
                }
            }
        }

        //3. 如果获取不到，则记录一下
        if(accessToken == null){
            LOGGER.debug("Cannot get access_token from request.");
        }

        return accessToken;
    }

    /**
     * 创建Access Token时需要做的其他操作
     */
    protected void onCreateAccessToken(PrincipalInfo principalInfo, SimpleAccessToken accessToken){
        this.tokenDAO.deleteOldAccessToken(principalInfo.getAccount());
        this.tokenDAO.updateAccessToken(accessToken);
    }

    /**
     * 创建Refresh Token时需要做的其他操作
     */
    protected void onCreateRefreshToken(SimpleAccessToken accessToken, SimpleRefreshToken refreshToken){
        this.tokenDAO.deleteOldRefreshToken(accessToken.getAccessToken());
        this.tokenDAO.updateRefreshToken(refreshToken);
    }

    /**
     * 移除过期不可用的Access Token
     */
    protected void removeExpiredAccessToken(Session session){
        //1. 获取Access Token
        SimpleAccessToken accessToken = (SimpleAccessToken) session.getAttribute(TokenSessionContext.SIMPLE_ACCESS_TOKEN_KEY);

        if(accessToken != null){
            LOGGER.debug(MessageFormat.format("Access Token [{0}] has expired.", accessToken.getAccessToken()));
            //2. 从DAO中删除
            this.tokenDAO.deleteAccessToken(accessToken.getAccessToken());
        }
    }

    /**
     * 移除过期的Access Token
     */
    protected void removeExpiredAccessToken(SimpleAccessToken accessToken){
        LOGGER.debug(MessageFormat.format("Access Token [{0}] has expired.", accessToken.getAccessToken()));

        this.tokenDAO.deleteAccessToken(accessToken.getAccessToken());
    }

    /**
     * 移除失效的Access Token
     */
    protected void removeInvalidAccessToken(String accessToken){
        LOGGER.debug(MessageFormat.format("Access Token [{0}] has invalid.", accessToken));

        this.tokenDAO.deleteAccessToken(accessToken);
    }

    /**
     * 移除过期的Refresh Token
     */
    protected void removeExpiredRefreshToken(SimpleRefreshToken refreshToken){
        LOGGER.debug(MessageFormat.format("Refresh Token [{0}] has invalid.", refreshToken.getRefreshToken()));

        this.tokenDAO.deleteRefreshToken(refreshToken.getRefreshToken());
    }

    /**
     * 在token模式中，这个方法已经废弃
     */
    @Override
    @Deprecated
    public void setGlobalTimeout(Long globalTimeout) {

    }

    /**
     * 在token模式中，这个方法已经废弃
     */
    @Override
    @Deprecated
    public void setGlobalTimeoutChronoUnit(ChronoUnit globalTimeoutChronoUnit) {

    }

    public TokenOperateResolver getTokenOperateResolver() {
        return tokenOperateResolver;
    }

    public void setTokenOperateResolver(TokenOperateResolver tokenOperateResolver) {
        this.tokenOperateResolver = tokenOperateResolver;
    }

    public TokenDAO getTokenDAO() {
        return tokenDAO;
    }

    public void setTokenDAO(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public int getTokenRetriesTimes() {
        return tokenRetriesTimes;
    }

    public void setTokenRetriesTimes(int tokenRetriesTimes) {
        this.tokenRetriesTimes = tokenRetriesTimes;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
