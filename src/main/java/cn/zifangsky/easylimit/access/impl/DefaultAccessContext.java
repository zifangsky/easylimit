package cn.zifangsky.easylimit.access.impl;

import cn.zifangsky.easylimit.access.AccessContext;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.exception.EasyLimitException;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.utils.MapContext;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 默认的{@link AccessContext}
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class DefaultAccessContext extends MapContext implements AccessContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAccessContext.class);

    private static final long serialVersionUID = 2035326578595189664L;

    /**
     * host的key
     */
    private static final String HOST_KEY = DefaultAccessContext.class.getName() + ":host";

    /**
     * sessionId的key
     */
    private static final String SESSION_ID_KEY = DefaultAccessContext.class.getName() + ":session_id";

    /**
     * {@link Session}的key
     */
    private static final String SESSION_KEY = DefaultAccessContext.class.getName() + ":session";

    /**
     * {@link ServletRequest}的key
     */
    private static final String SERVLET_REQUEST_KEY = DefaultAccessContext.class.getName() + ":servlet_request";

    /**
     * {@link ServletResponse}的key
     */
    private static final String SERVLET_RESPONSE_KEY = DefaultAccessContext.class.getName() + ":servlet_response";

    /**
     * {@link PrincipalInfo}的key
     */
    private static final String PRINCIPAL_INFO_KEY = DefaultAccessContext.class.getName() + ":principal_info";

    /**
     * {@link SecurityManager}的key
     */
    private static final String SECURITY_MANAGER_KEY = DefaultAccessContext.class.getName() + ":security_manager";

    /**
     * 当前登录成功的标识的key
     */
    private static final String AUTHENTICATED_KEY = DefaultAccessContext.class.getName() + ":authenticated";

    /**
     * 当前登录用户主体在{@link Session}中存储的KEY
     */
    public static final String PRINCIPAL_INFO_SESSION_KEY = DefaultAccessContext.class.getName() + ":principal_info_session";

    /**
     * 当前登录成功的标识在{@link Session}中存储的KEY
     */
    public static final String AUTHENTICATED_SESSION_KEY = DefaultAccessContext.class.getName() + ":authenticated_session";

    public DefaultAccessContext() {
    }

    public DefaultAccessContext(AccessContext accessContext) {
        super(accessContext);
    }

    @Override
    public String getHost() {
        return getByType(HOST_KEY, String.class);
    }

    @Override
    public void setHost(String host) {
        put(HOST_KEY, host);
    }

    @Override
    public ServletRequest getServletRequest() {
        return getByType(SERVLET_REQUEST_KEY, ServletRequest.class);
    }

    @Override
    public void setServletRequest(ServletRequest request) {
        put(SERVLET_REQUEST_KEY, request);
    }

    @Override
    public ServletResponse getServletResponse() {
        return getByType(SERVLET_RESPONSE_KEY, ServletResponse.class);
    }

    @Override
    public void setServletResponse(ServletResponse response) {
        put(SERVLET_RESPONSE_KEY, response);
    }

    @Override
    public Serializable getSessionId() {
        return getByType(SESSION_ID_KEY, Serializable.class);
    }

    @Override
    public void setSessionId(Serializable sessionId) {
        put(SESSION_ID_KEY, sessionId);
    }

    @Override
    public Session getSession() {
        return getByType(SESSION_KEY, Session.class);
    }

    @Override
    public void setSession(Session session) {
        put(SESSION_KEY, session);
    }

    @Override
    public PrincipalInfo getPrincipalInfo() {
        return getByType(PRINCIPAL_INFO_KEY, PrincipalInfo.class);
    }

    @Override
    public <T> void setPrincipalInfo(PrincipalInfo principalInfo) {
        put(PRINCIPAL_INFO_KEY, principalInfo);
    }

    @Override
    public SecurityManager getSecurityManager() {
        return getByType(SECURITY_MANAGER_KEY, SecurityManager.class);
    }

    @Override
    public void setSecurityManager(SecurityManager securityManager) {
        put(SECURITY_MANAGER_KEY, securityManager);
    }

    @Override
    public Boolean isAuthenticated() {
        return getByType(AUTHENTICATED_KEY, Boolean.class);
    }

    @Override
    public void setAuthenticated(boolean authc) {
        put(AUTHENTICATED_KEY, authc);
    }

    @Override
    public String acquireHost() {
        String host = this.getHost();

        //1. 如果为空，尝试从session获取
        if(host == null){
            Session session = this.acquireSession();

            if(session != null){
                host = session.getHost();
            }
        }

        //2. 如果为空，再次尝试从request获取
        if(host == null){
            ServletRequest request = this.acquireServletRequest();

            if(request != null){
                host = request.getRemoteHost();
            }
        }

        return host;
    }

    @Override
    public Session acquireSession() {
        return this.getSession();
    }

    @Override
    public PrincipalInfo acquirePrincipalInfo() {
        PrincipalInfo principalInfo = this.getPrincipalInfo();

        //如果为空，尝试从session获取
        if(principalInfo == null){
            Session session = this.acquireSession();

            if(session != null){
                principalInfo = (PrincipalInfo) session.getAttribute(PRINCIPAL_INFO_SESSION_KEY);
            }
        }

        return principalInfo;
    }

    @Override
    public Boolean acquireAuthenticated() {
        Boolean authenticated = this.isAuthenticated();

        //1. 如果为空，尝试从session获取
        if(authenticated == null){
            Session session = this.acquireSession();

            if(session != null){
                authenticated = (Boolean) session.getAttribute(AUTHENTICATED_SESSION_KEY);
            }
        }

        return authenticated;
    }

    @Override
    public SecurityManager acquireSecurityManager() {
        SecurityManager securityManager = this.getSecurityManager();

        //如果为空，尝试从SecurityUtils获取
        if(securityManager == null){
            try {
                securityManager = SecurityUtils.getSecurityManager();
            }catch (EasyLimitException e){
                LOGGER.error("There is no SecurityManager available", e);
            }
        }

        return securityManager;
    }

    @Override
    public ServletRequest acquireServletRequest() {
        return this.getServletRequest();
    }

    @Override
    public ServletResponse acquireServletResponse() {
        return this.getServletResponse();
    }
}
