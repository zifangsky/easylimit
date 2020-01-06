package cn.zifangsky.easylimit;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.impl.TokenAccessContext;
import cn.zifangsky.easylimit.access.impl.TokenAccessFactory;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.exception.token.TokenException;
import cn.zifangsky.easylimit.realm.Realm;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessRefreshToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;
import cn.zifangsky.easylimit.session.impl.support.TokenWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于token模式的{@link SecurityManager}
 *
 * @author zifangsky
 * @date 2019/6/3
 * @since 1.0.0
 */
public class TokenWebSecurityManager extends DefaultWebSecurityManager{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenWebSecurityManager.class);

    public TokenWebSecurityManager(Realm realm, TokenWebSessionManager sessionManager) {
        this(realm, sessionManager, new TokenAccessFactory());
    }

    public TokenWebSecurityManager(Realm realm, TokenWebSessionManager sessionManager, TokenAccessFactory accessFactory) {
        super(realm, sessionManager, accessFactory);
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
        TokenWebSessionManager sessionManager = (TokenWebSessionManager) this.getSessionManager();

        return sessionManager.getRefreshToken(refreshToken);
    }

    /**
     * 使用Refresh Token刷新Access Token
     * @author zifangsky
     * @date 2019/6/4 18:02
     * @since 1.0.0
     * @param simpleRefreshToken Refresh Token
     * @param principalInfo 用户主体信息
     * @param session session
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    public SimpleAccessRefreshToken refreshAccessToken(SimpleRefreshToken simpleRefreshToken, PrincipalInfo principalInfo, Session session){
        TokenWebSessionManager sessionManager = (TokenWebSessionManager) this.getSessionManager();

        return sessionManager.refreshAccessToken(simpleRefreshToken, principalInfo, session);
    }

    /**
     * 免认证登录（用于刷新Refresh Token，所以这一步不会创建Access Token）
     * @author zifangsky
     * @date 2020/1/6 14:17
     * @since 1.0.0
     * @param access access
     * @return cn.zifangsky.easylimit.access.Access
     */
    public Access loginWithNoAuthentication(Access access, ValidatedInfo validatedInfo) throws AuthenticationException {
        //1. 通过 realm 获取 principalInfo
        PrincipalInfo principalInfo = null;

        try {
            //免认证创建用户主体信息
            principalInfo = this.createPrincipalInfoWithNoAuthentication(validatedInfo);
        }catch (AuthenticationException e){
            //TODO RememberMe失败
            throw e;
        }

        //2. 重新创建Access
        return this.createAccess(principalInfo, access);
    }

    /**
     * 普通登录
     * @author zifangsky
     * @date 2019/6/4 18:02
     * @since 1.0.0
     * @param access access
     * @param validatedInfo 登录认证信息
     * @return cn.zifangsky.easylimit.access.Access
     */
    @Override
    public Access login(Access access, ValidatedInfo validatedInfo) throws AuthenticationException {
        //1. 通过 realm 获取 principalInfo
        PrincipalInfo principalInfo = null;

        try {
            //获取过程中会校验密码，如果密码校验失败将会抛出异常
            principalInfo = this.createPrincipalInfo(validatedInfo);
        }catch (AuthenticationException e){
            //TODO RememberMe失败
            throw e;
        }

        //2. 创建Access Token和Refresh Token
        TokenWebSessionManager sessionManager = (TokenWebSessionManager) this.getSessionManager();
        Session session = access.getSession(false);

        SimpleAccessToken accessToken = sessionManager.createAccessToken(principalInfo, session);
        SimpleRefreshToken refreshToken = sessionManager.createRefreshToken(validatedInfo, accessToken);

        //3. 判断是否需要踢出当前用户的旧会话，如果是则给旧会话添加一个“踢出”标识
        if(this.isKickOutOldSessions()){
            this.kickOutOldSessions(access, principalInfo);
        }

        //4. 重新创建Access
        return this.createAccess(principalInfo, access, accessToken, refreshToken);
    }

    /**
     * 用于登录成功之后重新创建{@link Access}
     * @author zifangsky
     * @date 2019/6/4 17:56
     * @since 1.0.0
     * @param principalInfo 登录用户主体信息
     * @param existAccess 已经存在的Access
     * @return cn.zifangsky.easylimit.access.Access
     */
    protected <T> Access createAccess(PrincipalInfo principalInfo, Access existAccess,
                                      SimpleAccessToken accessToken, SimpleRefreshToken refreshToken) {
        TokenAccessContext accessContext = this.createAccessContext();
        accessContext.setAuthenticated(true);
        accessContext.setPrincipalInfo(principalInfo);

        if(existAccess != null){
            accessContext.setServletRequest(existAccess.getServletRequest());
            accessContext.setServletResponse(existAccess.getServletResponse());
            accessContext.setSecurityManager(existAccess.getSecurityManager());
            accessContext.setSession(existAccess.getSession(false));
            accessContext.setSimpleAccessToken(accessToken);
            accessContext.setSimpleRefreshToken(refreshToken);
        }

        return this.createAccess(accessContext);
    }

    @Override
    protected TokenAccessContext createAccessContext() {
        return new TokenAccessContext();
    }
}
