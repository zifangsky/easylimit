package cn.zifangsky.easylimit;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.AccessContext;
import cn.zifangsky.easylimit.access.AccessFactory;
import cn.zifangsky.easylimit.access.impl.DefaultAccessContext;
import cn.zifangsky.easylimit.access.impl.DefaultAccessFactory;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.exception.authc.NoPermissionException;
import cn.zifangsky.easylimit.exception.authc.NoRoleException;
import cn.zifangsky.easylimit.exception.session.SessionException;
import cn.zifangsky.easylimit.permission.PermissionInfo;
import cn.zifangsky.easylimit.realm.Realm;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.SessionKey;
import cn.zifangsky.easylimit.session.SessionManager;
import cn.zifangsky.easylimit.session.impl.AbstractWebSessionManager;
import cn.zifangsky.easylimit.session.impl.DefaultSessionContext;
import cn.zifangsky.easylimit.session.impl.DefaultSessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;

/**
 * 默认的{@link SecurityManager}
 *
 * @author zifangsky
 * @date 2019/4/8
 * @since 1.0.0
 */
public class DefaultWebSecurityManager implements SecurityManager{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebSecurityManager.class);

    /**
     * Realm
     */
    private Realm realm;
    /**
     * SessionManager
     */
    private SessionManager sessionManager;
    /**
     * AccessFactory
     */
    private AccessFactory accessFactory;

    /**
     * 是否踢出当前用户的旧会话
     */
    private boolean kickOutOldSessions;

    public DefaultWebSecurityManager(Realm realm, SessionManager sessionManager) {
        this(realm, sessionManager, new DefaultAccessFactory());
    }

    public DefaultWebSecurityManager(Realm realm, SessionManager sessionManager, AccessFactory accessFactory) {
        this.realm = realm;
        this.sessionManager = sessionManager;
        this.accessFactory = accessFactory;
        //默认不踢出当前用户的旧会话
        this.kickOutOldSessions = false;
    }

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

        //2. 判断是否需要踢出当前用户的旧会话，如果是则给旧会话添加一个“踢出”标识
        if(this.isKickOutOldSessions()){
            this.kickOutOldSessions(access, principalInfo);
        }

        //3. 重新创建Access
        return this.createAccess(principalInfo, access);
    }

    @Override
    public void logout(Access access) {
        if(access == null){
            throw new IllegalArgumentException("Parameter access cannot be empty.");
        }

        //1. 清除realm中的缓存
        PrincipalInfo principalInfo = access.getPrincipalInfo();
        if(principalInfo != null){
            this.onLogout(principalInfo);
        }

        //2. 停止session
        this.stopSession(access);
    }


    protected void beforeLogout(Access access) {
        //TODO RememberMe删除
    }

    /**
     * 给用户旧会话添加一个“踢出”标识
     */
    protected void kickOutOldSessions(Access access, PrincipalInfo principalInfo){
        if(this.sessionManager instanceof AbstractWebSessionManager){
            AbstractWebSessionManager webSessionManager = (AbstractWebSessionManager) this.sessionManager;
            webSessionManager.kickOutOldSessions(principalInfo.getAccount(), access.getSession(false));
        }
    }

    /**
     * 用于登录成功之后重新创建{@link Access}
     * @author zifangsky
     * @date 2019/4/8 16:29
     * @since 1.0.0
     * @param principalInfo 登录用户主体信息
     * @param existAccess 已经存在的Access
     * @return cn.zifangsky.easylimit.access.Access
     */
    protected <T> Access createAccess(PrincipalInfo principalInfo, Access existAccess) {
        AccessContext accessContext = this.createAccessContext();
        accessContext.setAuthenticated(true);
        accessContext.setPrincipalInfo(principalInfo);

        if(existAccess != null){
            accessContext.setServletRequest(existAccess.getServletRequest());
            accessContext.setServletResponse(existAccess.getServletResponse());
            accessContext.setSecurityManager(existAccess.getSecurityManager());
            accessContext.setSession(existAccess.getSession(false));
        }

        return this.createAccess(accessContext);
    }

    @Override
    public Access createAccess(AccessContext accessContext) {
        //1. 设置初始的环境变量
        this.setHostToContext(accessContext);
        this.setSecurityManagerToContext(accessContext);
        this.setSessionToContext(accessContext);
        this.setPrincipalInfoToContext(accessContext);
        this.setAuthenticatedToContext(accessContext);

        //2. 调用真正的创建方法
        Access access = this.doCreateAccess(accessContext);

        //3. 将用户主体信息和登录状态保存到session
        this.saveToSession(accessContext);

        return access;
    }

    /**
     * 将用户主体信息和登录状态保存到session
     */
    protected void saveToSession(AccessContext accessContext) {
        Session session = accessContext.getSession();

        this.savePrincipalInfoToSession(session, accessContext.getPrincipalInfo());
        this.saveAuthenticatedToSession(session, accessContext.isAuthenticated());
    }

    /**
     * 将用户主体信息保存到session
     */
    protected void savePrincipalInfoToSession(Session session, PrincipalInfo principalInfo){
        if(principalInfo != null){
            session.setAttribute(DefaultAccessContext.PRINCIPAL_INFO_SESSION_KEY, principalInfo);
        }
    }

    /**
     * 将用户登录状态保存到session
     */
    protected void saveAuthenticatedToSession(Session session, Boolean isAuthenticated){
        session.setAttribute(DefaultAccessContext.AUTHENTICATED_SESSION_KEY, isAuthenticated);
    }

    /**
     * 调用工厂方法创建{@link Access}
     */
    protected Access doCreateAccess(AccessContext accessContext) {
        return getAccessFactory().createAccess(accessContext);
    }

    /**
     * 向{@link AccessContext}设置登录状态
     */
    protected void setAuthenticatedToContext(AccessContext accessContext) {
        Boolean authenticated = accessContext.acquireAuthenticated();
        if(authenticated != null && authenticated){
            accessContext.setAuthenticated(true);
        }else{
            accessContext.setAuthenticated(false);
        }
    }

    /**
     * 向{@link AccessContext}设置用户主体信息
     */
    protected void setPrincipalInfoToContext(AccessContext accessContext) {
        PrincipalInfo principalInfo = accessContext.acquirePrincipalInfo();
        if(principalInfo == null){
            //TODO 获取RememberMe中的用户主体
        }

        if(principalInfo != null){
            accessContext.setPrincipalInfo(principalInfo);
        }
    }

    /**
     * 向{@link AccessContext}设置Host
     */
    protected void setHostToContext(AccessContext accessContext) {
        String host = accessContext.acquireHost();
        if(host == null){
            host = "";
        }

        accessContext.setHost(host);
    }

    /**
     * 向{@link AccessContext}设置{@link Session}
     */
    protected void setSessionToContext(AccessContext accessContext) {
        Session session  = accessContext.acquireSession();
        if(session == null){
            session = this.acquireSessionByAccessContext(accessContext);
        }

        //3. 将session添加到accessContext
        if(session != null){
            accessContext.setSession(session);
            accessContext.setSessionId(session.getId());
        }
    }

    /**
     * 向{@link AccessContext}设置{@link SecurityManager}
     */
    protected void setSecurityManagerToContext(AccessContext accessContext) {
        SecurityManager securityManager = accessContext.acquireSecurityManager();
        if(securityManager == null){
            securityManager = this;
        }

        accessContext.setSecurityManager(securityManager);
    }

    /**
     * 通过{@link AccessContext}获取{@link Session}
     * @author zifangsky
     * @date 2019/4/8 18:05
     * @since 1.0.0
     * @param accessContext accessContext
     * @return cn.zifangsky.easylimit.session.Session
     */
    protected Session acquireSessionByAccessContext(AccessContext accessContext){
        //1. 获取sessionKey
        SessionKey sessionKey = this.getSessionKey(accessContext);

        //2. 通过sessionKey获取session
        return this.getSession(sessionKey, accessContext);
    }

    /**
     * 获取{@link SessionKey}
     * @author zifangsky
     * @date 2019/4/10 17:32
     * @since 1.0.0
     * @param accessContext accessContext
     * @return cn.zifangsky.easylimit.session.SessionKey
     */
    protected SessionKey getSessionKey(AccessContext accessContext) {
        //1. 尝试从accessContext获取sessionId
        Serializable sessionId = accessContext.getSessionId();
        if(sessionId == null && sessionManager instanceof AbstractWebSessionManager){
            //2. 再次尝试从ServletRequest获取
            AbstractWebSessionManager webSessionManager = (AbstractWebSessionManager) sessionManager;
            sessionId = webSessionManager.getSessionId(accessContext.acquireServletRequest(), accessContext.getServletResponse());
        }
        //Note: sessionId仍然可能获取不到，这就需要在后面重新创建session
        return new DefaultSessionKey(sessionId);
    }

    @Override
    public Session getSession(SessionKey key, AccessContext accessContext) {
        //1. 先通过sessionManager获取session
        Session session = null;
        try {
            session = sessionManager.getSession(key);
        }catch (SessionException e){
            LOGGER.error(MessageFormat.format("Session cannot be retrieved with SessionId[{0}].", key), e);
        }

        //2. 如果返回为空，则创建新session
        if(session == null){
            SessionContext sessionContext = new DefaultSessionContext(accessContext.getHost());
            session = this.createSession(sessionContext);
        }

        return session;
    }

    /**
     * 停用session
     * @author zifangsky
     * @date 2019/4/11 10:44
     * @since 1.0.0
     * @param access access
     */
    protected void stopSession(Access access){
        Session session = access.getSession(false);
        if(session != null){
            session.stop();;
        }
    }

    /**
     * 初始化{@link AccessContext}实例
     * @author zifangsky
     * @date 2019/4/8 14:26
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.access.AccessContext
     */
    protected AccessContext createAccessContext(){
        return new DefaultAccessContext();
    }

    @Override
    public Session createSession(SessionContext sessionContext) {
        return this.sessionManager.create(sessionContext);
    }

    @Override
    public PrincipalInfo createPrincipalInfo(ValidatedInfo validatedInfo) throws AuthenticationException{
        return this.realm.createPrincipalInfo(validatedInfo);
    }

    @Override
    public PrincipalInfo createPrincipalInfoWithNoAuthentication(ValidatedInfo validatedInfo) throws AuthenticationException {
        return this.realm.createPrincipalInfoWithNoAuthentication(validatedInfo);
    }

    @Override
    public PermissionInfo createPermissionInfo(PrincipalInfo principalInfo) {
        return this.realm.createPermissionInfo(principalInfo);
    }

    @Override
    public boolean hasPermission(PrincipalInfo principalInfo, String permission) {
        return this.realm.hasPermission(principalInfo, permission);
    }

    @Override
    public boolean hasAnyPermissions(PrincipalInfo principalInfo, String... permissions) {
        return this.realm.hasAnyPermissions(principalInfo, permissions);
    }

    @Override
    public boolean hasAnyPermissions(PrincipalInfo principalInfo, Collection<String> permissions) {
        return this.realm.hasAnyPermissions(principalInfo, permissions);
    }

    @Override
    public boolean hasAllPermissions(PrincipalInfo principalInfo, String... permissions) {
        return this.realm.hasAllPermissions(principalInfo, permissions);
    }

    @Override
    public boolean hasAllPermissions(PrincipalInfo principalInfo, Collection<String> permissions) {
        return this.realm.hasAllPermissions(principalInfo, permissions);
    }

    @Override
    public boolean hasRole(PrincipalInfo principalInfo, String role) {
        return this.realm.hasRole(principalInfo, role);
    }

    @Override
    public boolean hasAnyRoles(PrincipalInfo principalInfo, String... roles) {
        return this.realm.hasAnyRoles(principalInfo, roles);
    }

    @Override
    public boolean hasAnyRoles(PrincipalInfo principalInfo, Collection<String> roles) {
        return this.realm.hasAnyRoles(principalInfo, roles);
    }

    @Override
    public boolean hasAllRoles(PrincipalInfo principalInfo, String... roles) {
        return this.realm.hasAllRoles(principalInfo, roles);
    }

    @Override
    public boolean hasAllRoles(PrincipalInfo principalInfo, Collection<String> roles) {
        return this.realm.hasAllRoles(principalInfo, roles);
    }

    @Override
    public void checkPermission(PrincipalInfo principalInfo, String permission) throws NoPermissionException {
        this.realm.checkPermission(principalInfo, permission);
    }

    @Override
    public void checkAnyPermissions(PrincipalInfo principalInfo, String... permissions) throws NoPermissionException {
        this.realm.checkAnyPermissions(principalInfo, permissions);
    }

    @Override
    public void checkAnyPermissions(PrincipalInfo principalInfo, Collection<String> permissions) throws NoPermissionException {
        this.realm.checkAnyPermissions(principalInfo, permissions);
    }

    @Override
    public void checkAllPermissions(PrincipalInfo principalInfo, String... permissions) throws NoPermissionException {
        this.realm.checkAllPermissions(principalInfo, permissions);
    }

    @Override
    public void checkAllPermissions(PrincipalInfo principalInfo, Collection<String> permissions) throws NoPermissionException {
        this.realm.checkAllPermissions(principalInfo, permissions);
    }

    @Override
    public void checkRole(PrincipalInfo principalInfo, String role) throws NoRoleException {
        this.realm.checkRole(principalInfo, role);
    }

    @Override
    public void checkAnyRoles(PrincipalInfo principalInfo, String... roles) throws NoRoleException {
        this.realm.checkAnyRoles(principalInfo, roles);
    }

    @Override
    public void checkAnyRoles(PrincipalInfo principalInfo, Collection<String> roles) throws NoRoleException {
        this.realm.checkAnyRoles(principalInfo, roles);
    }

    @Override
    public void checkAllRoles(PrincipalInfo principalInfo, String... roles) throws NoRoleException {
        this.realm.checkAllRoles(principalInfo, roles);
    }

    @Override
    public void checkAllRoles(PrincipalInfo principalInfo, Collection<String> roles) throws NoRoleException {
        this.realm.checkAllRoles(principalInfo, roles);
    }

    @Override
    public void onLogout(PrincipalInfo principalInfo) {
        this.realm.onLogout(principalInfo);
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public AccessFactory getAccessFactory() {
        return accessFactory;
    }

    public void setAccessFactory(AccessFactory accessFactory) {
        this.accessFactory = accessFactory;
    }

    public boolean isKickOutOldSessions() {
        return kickOutOldSessions;
    }

    public void setKickOutOldSessions(boolean kickOutOldSessions) {
        this.kickOutOldSessions = kickOutOldSessions;
    }
}
