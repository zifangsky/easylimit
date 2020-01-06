package cn.zifangsky.easylimit.access.impl;

import cn.zifangsky.easylimit.TokenWebSecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.exception.token.TokenException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.impl.TokenSessionContext;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessRefreshToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 对外暴露的{@link Access}
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class ExposedTokenAccess extends ExposedAccess{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExposedTokenAccess.class);

    /**
     * Access Token
     */
    private SimpleAccessToken accessToken;
    /**
     * Refresh Token
     */
    private SimpleRefreshToken refreshToken;


    public ExposedTokenAccess(ServletRequest request, ServletResponse response, TokenWebSecurityManager securityManager) {
        super(request, response, securityManager);
    }

    public ExposedTokenAccess(ServletRequest request, ServletResponse response, String host, Session session,
                              Boolean authenticated, PrincipalInfo principalInfo, TokenWebSecurityManager securityManager,
                              SimpleAccessToken accessToken, SimpleRefreshToken refreshToken) {
        super(request, response, host, session, authenticated, principalInfo, securityManager);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * 使用Refresh Token刷新Access Token
     * @author zifangsky
     * @date 2019/6/4 18:03
     * @since 1.0.0
     * @param refreshToken Refresh Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    public SimpleAccessToken refreshAccessToken(String refreshToken) throws TokenException {
        TokenWebSecurityManager securityManager = (TokenWebSecurityManager) this.getSecurityManager();

        //1. 获取SimpleRefreshToken
        SimpleRefreshToken simpleRefreshToken = securityManager.getRefreshToken(refreshToken);

        //2. 如果当前处于未登录状态，则自动登录
        if(!this.getAuthenticated()){
            ValidatedInfo validatedInfo = simpleRefreshToken.getValidatedInfo();
            this.login(validatedInfo, false);
        }

        //3. 刷新Access Token
        SimpleAccessRefreshToken accessRefreshToken = securityManager.refreshAccessToken(simpleRefreshToken, this.getPrincipalInfo(), this.getSession());
        SimpleAccessToken newAccessToken = accessRefreshToken.getAccessToken();
        SimpleRefreshToken newRefreshToken = accessRefreshToken.getRefreshToken();

        this.setAccessToken(newAccessToken);
        this.setRefreshToken(newRefreshToken);

        return newAccessToken;
    }

    @Override
    public void login(ValidatedInfo validatedInfo) throws AuthenticationException {
        this.login(validatedInfo, true);
    }

    /**
     * 登录
     * @author zifangsky
     * @date 2019/6/30 16:19
     * @since 1.0.0
     * @param validatedInfo 表单中的用户名、密码
     * @param createToken 是否创建Token：普通登录需要创建；刷新Access Token在自动登录这一步不需要额外创建Token
     */
    public void login(ValidatedInfo validatedInfo, boolean createToken) throws AuthenticationException {
        //1. 调用securityManager执行登录操作
        TokenWebSecurityManager securityManager = (TokenWebSecurityManager) this.getSecurityManager();
        Access access = createToken ? securityManager.login(this, validatedInfo) : securityManager.loginWithNoAuthentication(this, validatedInfo);

        if(access instanceof ExposedTokenAccess){
            ExposedTokenAccess exposedAccess = (ExposedTokenAccess) access;

            //2. 获取正确的登录认证信息、host、Access Token、Refresh Token
            this.setPrincipalInfo(exposedAccess.getPrincipalInfo());
            this.setHost(exposedAccess.getHost());

            if(createToken){
                this.setAccessToken(exposedAccess.getAccessToken());
                this.setRefreshToken(exposedAccess.getRefreshToken());
            }
        }

        //3. 设置已经登录认证通过
        this.setAuthenticated(true);

        //4. 获取session
        Session session = access.getSession(false);
        if(session != null){
            this.setSession(session);
        }
    }

    @Override
    public void logout() {
        try {
            this.securityManager.logout(this);
        }catch (Exception e){
            LOGGER.error("The ExposedTokenAccess.logout() method threw an exception.", e);
        }finally {
            this.session = null;
            this.authenticated = false;
            this.principalInfo = null;
            this.accessToken = null;
            this.refreshToken = null;
        }
    }

    @Override
    protected SessionContext createSessionContext() {
        SessionContext sessionContext = new TokenSessionContext();
        sessionContext.setServletRequest(this.getRequest());
        sessionContext.setServletResponse(this.getResponse());
        sessionContext.setHost(this.getHost());

        return sessionContext;
    }

    public SimpleAccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(SimpleAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public SimpleRefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(SimpleRefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
}
