package cn.zifangsky.easylimit.access.impl;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.impl.support.AccessCallable;
import cn.zifangsky.easylimit.access.impl.support.AccessRunnable;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.exception.access.ExecutionException;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.impl.DefaultSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * 对外暴露的{@link Access}
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class ExposedAccess implements Access{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExposedAccess.class);

    /**
     * ServletRequest
     */
    private ServletRequest request;
    /**
     * ServletResponse
     */
    private ServletResponse response;
    /**
     * host
     */
    private String host;
    /**
     * Session
     */
    private Session session;
    /**
     * 是否已经登录认证通过
     */
    private Boolean authenticated;
    /**
     * 用户主体信息
     */
    private PrincipalInfo principalInfo;
    /**
     * 认证、权限、session等管理的入口
     */
    private SecurityManager securityManager;

    public ExposedAccess(ServletRequest request, ServletResponse response, SecurityManager securityManager) {
        this(request, response, null, null, false, null, securityManager);
    }

    public ExposedAccess(ServletRequest request, ServletResponse response, String host, Session session, Boolean authenticated, PrincipalInfo principalInfo, SecurityManager securityManager) {
        if(request == null){
            throw new IllegalArgumentException("Parameter request cannot be empty.");
        }
        if(response == null){
            throw new IllegalArgumentException("Parameter response cannot be empty.");
        }
        if(session == null){
            throw new IllegalArgumentException("Parameter session cannot be empty.");
        }
        if(securityManager == null){
            throw new IllegalArgumentException("Parameter securityManager cannot be empty.");
        }

        this.request = request;
        this.response = response;
        this.host = host;
        this.session = session;
        this.authenticated = authenticated != null ? authenticated : false;
        this.principalInfo = principalInfo;
        this.securityManager = securityManager;
    }

    /**
     * 判断是否拥有用户主体信息
     * @author zifangsky
     * @date 2019/4/8 11:22
     * @since 1.0.0
     * @return boolean
     */
    protected boolean hasPrincipal(){
        return principalInfo != null && principalInfo.getPrincipal() != null;
    }

    /**
     * 创建{@link SessionContext}
     * @author zifangsky
     * @date 2019/4/8 11:41
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.session.SessionContext
     */
    protected SessionContext createSessionContext(){
        SessionContext sessionContext = new DefaultSessionContext();
        sessionContext.setServletRequest(this.request);
        sessionContext.setServletResponse(this.response);
        sessionContext.setHost(this.host);

        return sessionContext;
    }

    @Override
    public ServletRequest getServletRequest() {
        return this.request;
    }

    @Override
    public void setServletRequest(ServletRequest request) {
        this.request = request;
    }

    @Override
    public ServletResponse getServletResponse() {
        return this.response;
    }

    @Override
    public void setServletResponse(ServletResponse response) {
        this.response = response;
    }

    @Override
    public SecurityManager getSecurityManager() {
        return this.securityManager;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.hasPrincipal() &&
                securityManager.hasPermission(this.principalInfo, permission);
    }

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        return this.hasPrincipal() &&
                securityManager.hasAnyPermissions(this.principalInfo, permissions);
    }

    @Override
    public boolean hasAnyPermissions(Collection<String> permissions) {
        return this.hasPrincipal() &&
                securityManager.hasAnyPermissions(this.principalInfo, permissions);
    }

    @Override
    public boolean hasAllPermissions(String... permissions) {
        return this.hasPrincipal() &&
                securityManager.hasAllPermissions(this.principalInfo, permissions);
    }

    @Override
    public boolean hasAllPermissions(Collection<String> permissions) {
        return this.hasPrincipal() &&
                securityManager.hasAllPermissions(this.principalInfo, permissions);
    }

    @Override
    public boolean hasRole(String role) {
        return this.hasPrincipal() &&
                securityManager.hasRole(this.principalInfo, role);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return this.hasPrincipal() &&
                securityManager.hasAnyRoles(this.principalInfo, roles);
    }

    @Override
    public boolean hasAnyRoles(Collection<String> roles) {
        return this.hasPrincipal() &&
                securityManager.hasAnyRoles(this.principalInfo, roles);
    }

    @Override
    public boolean hasAllRoles(String... roles) {
        return this.hasPrincipal() &&
                securityManager.hasAllRoles(this.principalInfo, roles);
    }

    @Override
    public boolean hasAllRoles(Collection<String> roles) {
        return this.hasPrincipal() &&
                securityManager.hasAllRoles(this.principalInfo, roles);
    }

    @Override
    public void login(ValidatedInfo validatedInfo) throws AuthenticationException {
        //1. 调用securityManager执行登录操作
        Access access = securityManager.login(this, validatedInfo);

        if(access instanceof ExposedAccess){
            ExposedAccess exposedAccess = (ExposedAccess) access;
            //2. 获取正确的登录认证信息和host
            this.principalInfo = exposedAccess.getPrincipalInfo();
            this.host = exposedAccess.getHost();
        }

        //3. 设置已经登录认证通过
        this.authenticated = true;

        //4. 获取session
        Session session = access.getSession(false);
        if(session != null){
            this.session = session;
        }
    }

    @Override
    public void logout() {
        try {
            this.securityManager.logout(this);
        }finally {
            this.session = null;
            this.authenticated = false;
            this.principalInfo = null;
        }
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public PrincipalInfo getPrincipalInfo() {
        return this.principalInfo;
    }

    @Override
    public Session getSession() {
        return this.getSession(true);
    }

    @Override
    public Session getSession(boolean create) {
        //session不存在，创建session
        if(this.session == null && create){
            //1. 创建SessionContext
            SessionContext sessionContext = this.createSessionContext();
            //2. 调用securityManager的方法创建session
            this.session = this.securityManager.createSession(sessionContext);
        }

        return this.session;
    }

    @Override
    public <V> V execute(Callable<V> callable) throws ExecutionException {
        Callable<V> bindCallable = this.bindWith(callable);
        try {
            return bindCallable.call();
        }catch (Exception e){
            throw new ExecutionException(e);
        }
    }

    @Override
    public void execute(Runnable runnable) throws ExecutionException {
        Runnable bindRunnable = this.bindWith(runnable);
        bindRunnable.run();
    }

    @Override
    public <T> Callable<T> bindWith(Callable<T> callable) {
        return new AccessCallable<T>(this, callable);
    }

    @Override
    public Runnable bindWith(Runnable runnable) {
        return new AccessRunnable(this, runnable);
    }

    public ServletRequest getRequest() {
        return request;
    }

    public void setRequest(ServletRequest request) {
        this.request = request;
    }

    public ServletResponse getResponse() {
        return response;
    }

    public void setResponse(ServletResponse response) {
        this.response = response;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setPrincipalInfo(PrincipalInfo principalInfo) {
        this.principalInfo = principalInfo;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}
