package cn.zifangsky.easylimit.permission.impl;

import cn.zifangsky.easylimit.permission.PermissionInfo;

import java.util.Set;

/**
 * {@link PermissionInfo}的基本实现
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class SimplePermissionInfo implements PermissionInfo{
    /**
     * 当前用户拥有的所有角色
     */
    private Set<String> roles;

    /**
     * 当前用户拥有的所有权限
     */
    private Set<String> permissions;

    public SimplePermissionInfo() {

    }

    public SimplePermissionInfo(Set<String> roles, Set<String> permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public Set<String> getPermissions() {
        return this.permissions;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
