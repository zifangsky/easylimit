package cn.zifangsky.easylimit.access;

import cn.zifangsky.easylimit.access.impl.DefaultAccessContext;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.utils.SecurityUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * 访问实例，每发起一个请求将相应创建一个{@link Access}
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public interface Access {

    /**
     * 获取{@link ServletRequest}
     * @author zifangsky
     * @date 2019/4/4 14:06
     * @since 1.0.0
     * @return javax.servlet.ServletRequest
     */
    ServletRequest getServletRequest();

    /**
     * 设置{@link ServletRequest}
     * @author zifangsky
     * @date 2019/4/4 14:07
     * @since 1.0.0
     * @param request request
     */
    void setServletRequest(ServletRequest request);

    /**
     * 获取{@link ServletResponse}
     * @author zifangsky
     * @date 2019/4/4 14:09
     * @since 1.0.0
     * @return javax.servlet.ServletResponse
     */
    ServletResponse getServletResponse();

    /**
     * 设置{@link ServletResponse}
     * @author zifangsky
     * @date 2019/4/4 14:09
     * @since 1.0.0
     * @param response response
     */
    void setServletResponse(ServletResponse response);

    /**
     * 获取{@link SecurityManager}
     * @author zifangsky
     * @date 2019/4/4 17:02
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.SecurityManager
     */
    SecurityManager getSecurityManager();

    /**
     * 判断是否拥有某个权限
     *
     * @param permission 权限CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 14:45
     * @since 1.0.0
     */
    boolean hasPermission(String permission);

    /**
     * 判断是否拥有其中某个权限
     *
     * @param permissions 所有可能的权限CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    boolean hasAnyPermissions(String... permissions);

    /**
     * 判断是否拥有其中某个权限
     *
     * @param permissions 所有可能的权限CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    boolean hasAnyPermissions(Collection<String> permissions);

    /**
     * 判断是否拥有其中所有权限
     *
     * @param permissions 所有可能的权限CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    boolean hasAllPermissions(String... permissions);

    /**
     * 判断是否拥有其中所有权限
     *
     * @param permissions 所有可能的权限CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    boolean hasAllPermissions(Collection<String> permissions);

    /**
     * 判断是否拥有某个角色
     *
     * @param role 角色CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    boolean hasRole(String role);

    /**
     * 判断是否拥有其中某个角色
     *
     * @param roles 所有可能的角色CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    boolean hasAnyRoles(String... roles);

    /**
     * 判断是否拥有其中某个角色
     *
     * @param roles 所有可能的角色CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    boolean hasAnyRoles(Collection<String> roles);

    /**
     * 判断是否拥有其中所有角色
     *
     * @param roles 所有可能的角色CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    boolean hasAllRoles(String... roles);

    /**
     * 判断是否拥有其中所有角色
     *
     * @param roles 所有可能的角色CODE
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    boolean hasAllRoles(Collection<String> roles);

    /**
     * 登录认证
     *
     * @param validatedInfo 待验证的用户名、密码
     * @author zifangsky
     * @date 2019/4/3 16:25
     * @since 1.0.0
     * @throws AuthenticationException AuthenticationException
     */
    void login(ValidatedInfo validatedInfo) throws AuthenticationException;

    /**
     * 注销登录
     *
     * @author zifangsky
     * @date 2019/4/3 16:29
     * @since 1.0.0
     */
    void logout();

    /**
     * 返回是否已经登录认证通过
     *
     * @return boolean
     * @author zifangsky
     * @date 2019/4/3 16:22
     * @since 1.0.0
     */
    boolean isAuthenticated();

    /**
     * 获取正确的登录认证信息
     * @author zifangsky
     * @date 2019/4/3 14:44
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.authc.Principal
     */
    PrincipalInfo getPrincipalInfo();

    /**
     * 获取{@link Session}，如果已经存在则直接获取，否则创建新的{@link Session}
     * @author zifangsky
     * @date 2019/4/8 11:54
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.session.Session
     */
    public Session getSession();

    /**
     * 获取{@link Session}
     *
     * @param create 是否创建
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/4/3 16:30
     * @since 1.0.0
     */
    Session getSession(boolean create);

    /**
     * 执行任务（一般用于在{@link javax.servlet.Filter}中执行其他过滤链）
     *
     * @param callable callable
     * @return V
     * @author zifangsky
     * @date 2019/4/3 16:31
     * @since 1.0.0
     * @throws ExecutionException ExecutionException
     */
    <V> V execute(Callable<V> callable) throws ExecutionException;

    /**
     * 执行任务，且无返回结果
     *
     * @param runnable runnable
     * @author zifangsky
     * @date 2019/4/3 16:32
     * @since 1.0.0
     * @throws ExecutionException ExecutionException
     */
    void execute(Runnable runnable) throws ExecutionException;

    /**
     * 在执行任务前，将{@link Access}和{@link SecurityManager}绑定到ThreadLocal
     * @author zifangsky
     * @date 2019/4/4 15:31
     * @since 1.0.0
     * @param callable 具体需要执行的任务
     * @return java.util.concurrent.Callable<T>
     */
    <T> Callable<T> bindWith(Callable<T> callable);

    /**
     * 在执行任务前，将{@link Access}和{@link SecurityManager}绑定到ThreadLocal
     * @author zifangsky
     * @date 2019/4/4 15:32
     * @since 1.0.0
     * @param runnable 具体需要执行的任务
     * @return java.lang.Runnable
     */
    Runnable bindWith(Runnable runnable);

    /* ************ 构建类 *************** */
    /**
     * Access的构建类
     */
    class Builder {
        /**
         * 初始数据
         */
        private AccessContext accessContext;
        /**
         * SecurityManager
         */
        private SecurityManager securityManager;
        /**
         * ServletRequest
         */
        private ServletRequest servletRequest;
        /**
         * ServletResponse
         */
        private ServletResponse servletResponse;

        public Builder(ServletRequest servletRequest, ServletResponse servletResponse) {
            this(SecurityUtils.getSecurityManager(), servletRequest, servletResponse);
        }

        public Builder(SecurityManager securityManager, ServletRequest servletRequest, ServletResponse servletResponse) {
            if(servletRequest == null){
                throw new IllegalArgumentException("Parameter servletRequest cannot be empty.");
            }
            if(servletResponse == null){
                throw new IllegalArgumentException("Parameter servletResponse cannot be empty.");
            }
            if(securityManager == null){
                throw new IllegalArgumentException("Parameter securityManager cannot be empty.");
            }

            this.securityManager = securityManager;
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;

            //初始化AccessContext
            this.accessContext = this.createAccessContext();
            this.accessContext.setSecurityManager(securityManager);
            this.accessContext.setServletRequest(servletRequest);
            this.accessContext.setServletResponse(servletResponse);
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

        /**
         * 通过{@link AccessContext}创建{@link Access}实例
         */
        public Access build(){
            return this.securityManager.createAccess(this.accessContext);
        }

    }

}
