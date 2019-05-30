package cn.zifangsky.easylimit.permission;

import java.util.Set;

/**
 * 角色+权限信息
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public interface PermissionInfo {
    /**
     * 获取所有角色信息
     * @author zifangsky
     * @date 2019/4/4 11:07
     * @since 1.0.0
     * @return java.util.Set<java.lang.String>
     */
    Set<String> getRoles();

    /**
     * 获取所有权限信息
     * @author zifangsky
     * @date 2019/4/4 11:07
     * @since 1.0.0
     * @return java.util.Set<java.lang.String>
     */
    Set<String> getPermissions();
}
