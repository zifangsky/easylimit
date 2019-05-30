package cn.zifangsky.easylimit.realm.impl;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.exception.authc.NoPermissionInfoException;
import cn.zifangsky.easylimit.permission.PermissionInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 实现角色、权限相关方法
 *
 * @author zifangsky
 * @date 2019/4/11
 * @since 1.0.0
 */
public abstract class AbstractPermissionRealm extends AbstractAuthenticationRealm{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPermissionRealm.class);

    /**
     * 默认的{@link PermissionInfo}缓存名称
     */
    private static final String DEFAULT_PERMISSION_INFO_CACHE_NAME = Constants.PROJECT_NAME + ":permission_info_cache";

    /**
     * 是否缓存{@link PermissionInfo}
     * <p>Note: 一般都缓存角色、权限信息</p>
     */
    private boolean enablePermissionInfoCache = false;

    /**
     * {@link PermissionInfo}的缓存管理器
     */
    private Cache<String, PermissionInfo> permissionInfoCache;

    /**
     * {@link PermissionInfo}缓存名称
     */
    private String permissionInfoCacheName;

    public AbstractPermissionRealm() {
        this(null, null);
    }

    public AbstractPermissionRealm(Cache<String, PermissionInfo> permissionInfoCache) {
        this(permissionInfoCache, DEFAULT_PERMISSION_INFO_CACHE_NAME);
    }

    public AbstractPermissionRealm(Cache<String, PermissionInfo> permissionInfoCache, String permissionInfoCacheName) {
        super();
        if(permissionInfoCache != null){
            this.enablePermissionInfoCache = true;
            this.permissionInfoCache = permissionInfoCache;
        }

        if(permissionInfoCacheName != null){
            this.permissionInfoCacheName = permissionInfoCacheName;
        }
    }

    /**
     * 真正的获取角色、权限相关信息的逻辑
     * @author zifangsky
     * @date 2019/4/11 18:19
     * @since 1.0.0
     * @param principalInfo 用户主体信息
     * @return cn.zifangsky.easylimit.authc.PrincipalInfo
     */
    protected abstract PermissionInfo doGetPermissionInfo(PrincipalInfo principalInfo);

    @Override
    protected void doClearCache(PrincipalInfo principalInfo) {
        super.doClearCache(principalInfo);
        this.clearCachedPermissionInfo(principalInfo);
    }

    @Override
    public PermissionInfo createPermissionInfo(PrincipalInfo principalInfo) {
        if(principalInfo == null){
            throw new IllegalArgumentException("Parameter principalInfo cannot be empty.");
        }

        //1. 获取缓存的角色、权限相关信息
        PermissionInfo permissionInfo = this.getCachedPermissionInfo(principalInfo);

        //2. 如果缓存中没有，则尝试调用数据库查询
        if(permissionInfo == null){
            permissionInfo = this.doGetPermissionInfo(principalInfo);

            if(permissionInfo != null){
                //尝试缓存角色、权限相关信息
                this.cachePermissionInfo(principalInfo, permissionInfo);
            }
        }

        //3. 如果还为空，则抛出异常
        if(permissionInfo == null){
            String msg = MessageFormat.format("No PermissionInfo found for submitted PrincipalInfo[(0)].", principalInfo);
            LOGGER.error(msg);
            throw new NoPermissionInfoException(msg);
        }

        return permissionInfo;
    }

    /**
     * 获取缓存的角色、权限相关信息
     */
    private PermissionInfo getCachedPermissionInfo(PrincipalInfo principalInfo){
        if(enablePermissionInfoCache && permissionInfoCache != null){
            //获取缓存的KEY
            String cacheKey = this.getPermissionInfoCacheKey(principalInfo);
            if(cacheKey != null){
                PermissionInfo result = permissionInfoCache.get(permissionInfoCacheName, cacheKey);
                LOGGER.debug(MessageFormat.format("get cached PermissionInfo, key:[{0}], value:[{1}].", cacheKey, result));

                return result;
            }
        }

        return null;
    }

    /**
     * 尝试缓存角色、权限相关信息
     */
    private void cachePermissionInfo(PrincipalInfo principalInfo, PermissionInfo permissionInfo){
        if(enablePermissionInfoCache && permissionInfoCache != null){
            //获取缓存的KEY
            String cacheKey = this.getPermissionInfoCacheKey(principalInfo);
            if(cacheKey != null){
                //缓存
                permissionInfoCache.put(permissionInfoCacheName, cacheKey, permissionInfo);
            }
        }
    }

    protected void clearCachedPermissionInfo(PrincipalInfo principalInfo){
        if(principalInfo != null && enablePermissionInfoCache && permissionInfoCache != null){
            //获取缓存的KEY
            String cacheKey = this.getPermissionInfoCacheKey(principalInfo);
            if(cacheKey != null){
                //清除缓存
                permissionInfoCache.remove(permissionInfoCacheName, cacheKey);
            }
        }
    }

    protected String getPermissionInfoCacheKey(PrincipalInfo principalInfo){
        return principalInfo.getAccount();
    }

    protected Set<String> getRoles(PermissionInfo permissionInfo){
        Set<String> result = new HashSet<>();

        if(permissionInfo != null){
            Set<String> roles = permissionInfo.getRoles();
            if(roles != null && roles.size() > 0){
                result.addAll(roles);
            }
        }

        return result;
    }

    protected Set<String> getPermissions(PermissionInfo permissionInfo){
        Set<String> result = new HashSet<>();

        if(permissionInfo != null){
            Set<String> permissions = permissionInfo.getPermissions();
            if(permissions != null && permissions.size() > 0){
                result.addAll(permissions);
            }
        }

        return result;
    }

    /**
     * 判断是否拥有某个角色
     * @author zifangsky
     * @date 2019/4/12 14:32
     * @since 1.0.0
     * @param permissionInfo 所有的角色、权限信息
     * @param role 某个单个角色
     * @return boolean
     */
    protected boolean hasRole(PermissionInfo permissionInfo, String role) {
        if(StringUtils.isBlank(role)){
            throw new IllegalArgumentException("Parameter role cannot be empty.");
        }

        Set<String> roleSet = this.getRoles(permissionInfo);
        return roleSet.contains(role);
    }

    /**
     * 判断是否拥有其中某个角色
     * @author zifangsky
     * @date 2019/4/12 14:32
     * @since 1.0.0
     * @param permissionInfo 所有的角色、权限信息
     * @param roles 所有可能的角色
     * @return boolean
     */
    protected boolean hasAnyRoles(PermissionInfo permissionInfo, Collection<String> roles) {
        if(roles == null){
            throw new IllegalArgumentException("Parameter roles cannot be empty.");
        }

        Set<String> roleSet = this.getRoles(permissionInfo);
        for(String tmp : roles){
            if(roleSet.contains(tmp)){
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否拥有其中所有角色
     * @author zifangsky
     * @date 2019/4/12 14:32
     * @since 1.0.0
     * @param permissionInfo 所有的角色、权限信息
     * @param roles 所有可能的角色
     * @return boolean
     */
    protected boolean hasAllRoles(PermissionInfo permissionInfo, Collection<String> roles) {
        if(roles == null){
            throw new IllegalArgumentException("Parameter roles cannot be empty.");
        }

        Set<String> roleSet = this.getRoles(permissionInfo);
        for(String tmp : roles){
            if(!roleSet.contains(tmp)){
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否拥有某个权限
     * @author zifangsky
     * @date 2019/4/12 13:32
     * @since 1.0.0
     * @param permissionInfo 所有的角色、权限信息
     * @param permission 某个单个权限
     * @return boolean
     */
    protected boolean hasPermission(PermissionInfo permissionInfo, String permission) {
        if(StringUtils.isBlank(permission)){
            throw new IllegalArgumentException("Parameter permission cannot be empty.");
        }

        Set<String> permissionSet = this.getPermissions(permissionInfo);
        return permissionSet.contains(permission);
    }

    /**
     * 判断是否拥有其中某个权限
     * @author zifangsky
     * @date 2019/4/12 13:32
     * @since 1.0.0
     * @param permissionInfo 所有的角色、权限信息
     * @param permissions 所有可能的权限
     * @return boolean
     */
    protected boolean hasAnyPermissions(PermissionInfo permissionInfo, Collection<String> permissions) {
        if(permissions == null){
            throw new IllegalArgumentException("Parameter permissions cannot be empty.");
        }

        Set<String> permissionSet = this.getPermissions(permissionInfo);
        for(String tmp : permissions){
            if(permissionSet.contains(tmp)){
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否拥有其中所有权限
     * @author zifangsky
     * @date 2019/4/12 13:32
     * @since 1.0.0
     * @param permissionInfo 所有的角色、权限信息
     * @param permissions 所有可能的权限
     * @return boolean
     */
    protected boolean hasAllPermissions(PermissionInfo permissionInfo, Collection<String> permissions) {
        if(permissions == null){
            throw new IllegalArgumentException("Parameter permissions cannot be empty.");
        }

        Set<String> permissionSet = this.getPermissions(permissionInfo);
        for(String tmp : permissions){
            if(!permissionSet.contains(tmp)){
                return false;
            }
        }

        return true;
    }


    @Override
    public boolean hasPermission(PrincipalInfo principalInfo, String permission) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);
        return this.hasPermission(permissionInfo, permission);
    }

    @Override
    public boolean hasAnyPermissions(PrincipalInfo principalInfo, String... permissions) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);

        List<String> list = Arrays.asList(permissions);
        return this.hasAnyPermissions(permissionInfo, list);
    }

    @Override
    public boolean hasAnyPermissions(PrincipalInfo principalInfo, Collection<String> permissions) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);
        return this.hasAnyPermissions(permissionInfo, permissions);
    }

    @Override
    public boolean hasAllPermissions(PrincipalInfo principalInfo, String... permissions) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);

        List<String> list = Arrays.asList(permissions);
        return this.hasAllPermissions(permissionInfo, list);
    }

    @Override
    public boolean hasAllPermissions(PrincipalInfo principalInfo, Collection<String> permissions) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);
        return this.hasAllPermissions(permissionInfo, permissions);
    }

    @Override
    public boolean hasRole(PrincipalInfo principalInfo, String role) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);
        return this.hasRole(permissionInfo, role);
    }

    @Override
    public boolean hasAnyRoles(PrincipalInfo principalInfo, String... roles) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);

        List<String> list = Arrays.asList(roles);
        return this.hasAnyRoles(permissionInfo, list);
    }

    @Override
    public boolean hasAnyRoles(PrincipalInfo principalInfo, Collection<String> roles) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);
        return this.hasAnyRoles(permissionInfo, roles);
    }

    @Override
    public boolean hasAllRoles(PrincipalInfo principalInfo, String... roles) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);

        List<String> list = Arrays.asList(roles);
        return this.hasAllRoles(permissionInfo, list);
    }

    @Override
    public boolean hasAllRoles(PrincipalInfo principalInfo, Collection<String> roles) {
        PermissionInfo permissionInfo = this.createPermissionInfo(principalInfo);
        return this.hasAllRoles(permissionInfo, roles);
    }

    public boolean isEnablePermissionInfoCache() {
        return enablePermissionInfoCache;
    }

    public void setEnablePermissionInfoCache(boolean enablePermissionInfoCache) {
        this.enablePermissionInfoCache = enablePermissionInfoCache;
    }

    public Cache<String, PermissionInfo> getPermissionInfoCache() {
        return permissionInfoCache;
    }

    public void setPermissionInfoCache(Cache<String, PermissionInfo> permissionInfoCache) {
        this.permissionInfoCache = permissionInfoCache;
    }

    public String getPermissionInfoCacheName() {
        return permissionInfoCacheName;
    }

    public void setPermissionInfoCacheName(String permissionInfoCacheName) {
        this.permissionInfoCacheName = permissionInfoCacheName;
    }
}
