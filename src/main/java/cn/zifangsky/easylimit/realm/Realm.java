package cn.zifangsky.easylimit.realm;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.exception.authc.NoPermissionException;
import cn.zifangsky.easylimit.exception.authc.NoRoleException;
import cn.zifangsky.easylimit.permission.PermissionInfo;

import java.util.Collection;

/**
 * 角色、权限相关校验的合集
 * <p>Note: 后面通过装饰模式调用</p>
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public interface Realm {

    /**
     * 通过登录认证信息创建正确的认证信息
     * @author zifangsky
     * @date 2019/4/3 18:29
     * @since 1.0.0
     * @param validatedInfo 登录认证信息
     * @return cn.zifangsky.easylimit.authc.Principal
     * @throws AuthenticationException AuthenticationException
     */
    PrincipalInfo createPrincipalInfo(ValidatedInfo validatedInfo) throws AuthenticationException;

    /**
     * 通过用户主体创建“角色+权限”信息
     * @author zifangsky
     * @date 2019/4/4 11:34
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @return PermissionInfo
     */
    PermissionInfo createPermissionInfo(PrincipalInfo principalInfo);

    /**
     * 判断是否拥有某个权限
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param permission 权限CODE
     * @return boolean
     */
    boolean hasPermission(PrincipalInfo principalInfo, String permission);

    /**
     * 判断是否拥有其中某个权限
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @return boolean
     */
    boolean hasAnyPermissions(PrincipalInfo principalInfo, String... permissions);

    /**
     * 判断是否拥有其中某个权限
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @return boolean
     */
    boolean hasAnyPermissions(PrincipalInfo principalInfo, Collection<String> permissions);

    /**
     * 判断是否拥有其中所有权限
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @return boolean
     */
    boolean hasAllPermissions(PrincipalInfo principalInfo, String... permissions);

    /**
     * 判断是否拥有其中所有权限
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @return boolean
     */
    boolean hasAllPermissions(PrincipalInfo principalInfo, Collection<String> permissions);

    /**
     * 判断是否拥有某个角色
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param role 角色CODE
     * @return boolean
     */
    boolean hasRole(PrincipalInfo principalInfo, String role);

    /**
     * 判断是否拥有其中某个角色
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @return boolean
     */
    boolean hasAnyRoles(PrincipalInfo principalInfo, String... roles);

    /**
     * 判断是否拥有其中某个角色
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @return boolean
     */
    boolean hasAnyRoles(PrincipalInfo principalInfo, Collection<String> roles);

    /**
     * 判断是否拥有其中所有角色
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @return boolean
     */
    boolean hasAllRoles(PrincipalInfo principalInfo, String... roles);

    /**
     * 判断是否拥有其中所有角色
     * @author zifangsky
     * @date 2019/4/4 11:30
     * @since 1.0.0
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @return boolean
     */
    boolean hasAllRoles(PrincipalInfo principalInfo, Collection<String> roles);

    /**
     * 判断是否拥有某个权限
     *
     * @param principalInfo 用户主体
     * @param permission 权限CODE
     * @throws NoPermissionException 没有某个权限的异常
     * @author zifangsky
     * @date 2019/4/3 14:45
     * @since 1.0.0
     */
    void checkPermission(PrincipalInfo principalInfo, String permission) throws NoPermissionException;

    /**
     * 判断是否拥有其中某个权限
     *
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @throws NoPermissionException 没有某个权限的异常
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    void checkAnyPermissions(PrincipalInfo principalInfo, String... permissions) throws NoPermissionException;

    /**
     * 判断是否拥有其中某个权限
     *
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @throws NoPermissionException 没有某个权限的异常
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    void checkAnyPermissions(PrincipalInfo principalInfo, Collection<String> permissions) throws NoPermissionException;

    /**
     * 判断是否拥有其中所有权限
     *
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @throws NoPermissionException 没有某个权限的异常
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    void checkAllPermissions(PrincipalInfo principalInfo, String... permissions) throws NoPermissionException;

    /**
     * 判断是否拥有其中所有权限
     *
     * @param principalInfo 用户主体
     * @param permissions 所有可能的权限CODE
     * @throws NoPermissionException 没有某个权限的异常
     * @author zifangsky
     * @date 2019/4/3 14:50
     * @since 1.0.0
     */
    void checkAllPermissions(PrincipalInfo principalInfo, Collection<String> permissions) throws NoPermissionException;

    /**
     * 判断是否拥有某个角色
     *
     * @param principalInfo 用户主体
     * @param role 角色CODE
     * @throws NoRoleException 没有某个角色的异常
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    void checkRole(PrincipalInfo principalInfo, String role) throws NoRoleException;

    /**
     * 判断是否拥有其中某个角色
     *
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @throws NoRoleException 没有某个角色的异常
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    void checkAnyRoles(PrincipalInfo principalInfo, String... roles) throws NoRoleException;

    /**
     * 判断是否拥有其中某个角色
     *
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @throws NoRoleException 没有某个角色的异常
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    void checkAnyRoles(PrincipalInfo principalInfo, Collection<String> roles) throws NoRoleException;

    /**
     * 判断是否拥有其中所有角色
     *
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @throws NoRoleException 没有某个角色的异常
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    void checkAllRoles(PrincipalInfo principalInfo, String... roles) throws NoRoleException;

    /**
     * 判断是否拥有其中所有角色
     *
     * @param principalInfo 用户主体
     * @param roles 所有可能的角色CODE
     * @throws NoRoleException 没有某个角色的异常
     * @author zifangsky
     * @date 2019/4/3 15:19
     * @since 1.0.0
     */
    void checkAllRoles(PrincipalInfo principalInfo, Collection<String> roles) throws NoRoleException;

    /**
     * 退出登录时的操作
     * @author zifangsky
     * @date 2019/4/10 18:29
     * @since 1.0.0
     * @param principalInfo 用户主体
     */
    void onLogout(PrincipalInfo principalInfo);
}
