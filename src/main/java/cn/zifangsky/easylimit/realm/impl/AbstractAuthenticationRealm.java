package cn.zifangsky.easylimit.realm.impl;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.authc.impl.UsernamePasswordValidatedInfo;
import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.EncryptionTypeEnums;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.exception.authc.IncorrectCredentialsException;
import cn.zifangsky.easylimit.exception.authc.NoPrincipalInfoException;
import cn.zifangsky.easylimit.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * 实现登录、注销相关方法
 *
 * @author zifangsky
 * @date 2019/4/11
 * @since 1.0.0
 */
public abstract class AbstractAuthenticationRealm implements Realm{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuthenticationRealm.class);

    /**
     * 默认的{@link PrincipalInfo}缓存名称
     */
    private static final String DEFAULT_PRINCIPAL_INFO_CACHE_NAME = Constants.PROJECT_NAME + ":principal_info_cache";

    /**
     * 是否缓存{@link PrincipalInfo}
     * <p>Note: 一般不缓存用户主体信息</p>
     */
    private boolean enablePrincipalInfoCache = false;

    /**
     * {@link PrincipalInfo}的缓存管理器
     */
    private Cache<String, PrincipalInfo> principalInfoCache;

    /**
     * {@link PrincipalInfo}缓存名称
     */
    private String principalInfoCacheName;

    public AbstractAuthenticationRealm() {
        this(null, null);
    }

    public AbstractAuthenticationRealm(Cache<String, PrincipalInfo> principalInfoCache) {
        this(principalInfoCache, DEFAULT_PRINCIPAL_INFO_CACHE_NAME);
    }

    public AbstractAuthenticationRealm(Cache<String, PrincipalInfo> principalInfoCache, String principalInfoCacheName) {
        if(principalInfoCache != null){
            this.enablePrincipalInfoCache = true;
            this.principalInfoCache = principalInfoCache;

            if(principalInfoCacheName != null){
                this.principalInfoCacheName = principalInfoCacheName;
            }else{
                this.principalInfoCacheName = DEFAULT_PRINCIPAL_INFO_CACHE_NAME;
            }
        }
    }

    /**
     * 真正的获取正确的用户主体信息的逻辑
     * @author zifangsky
     * @date 2019/4/11 15:47
     * @since 1.0.0
     * @param validatedInfo 登录时来至外部的需要验证的信息
     * @return cn.zifangsky.easylimit.authc.PrincipalInfo
     * @throws AuthenticationException AuthenticationException
     */
    protected abstract PrincipalInfo doGetPrincipalInfo(ValidatedInfo validatedInfo) throws AuthenticationException;

    @Override
    public PrincipalInfo createPrincipalInfo(ValidatedInfo validatedInfo) throws AuthenticationException {
        if(validatedInfo == null){
            throw new IllegalArgumentException("Parameter validatedInfo cannot be empty.");
        }

        //1. 获取缓存的实体信息
        PrincipalInfo principalInfo = this.getCachedPrincipalInfo(validatedInfo);

        //2. 如果缓存中没有，则尝试调用数据库查询
        if(principalInfo == null){
            principalInfo = this.doGetPrincipalInfo(validatedInfo);

            if(principalInfo != null){
                //尝试缓存用户实体信息
                this.cachePrincipalInfo(validatedInfo, principalInfo);
            }
        }

        //3. 校验用户名密码
        if(principalInfo != null){
            this.checkCredentialsInfo(validatedInfo, principalInfo);
        }else{
            String msg = MessageFormat.format("No PrincipalInfo found for submitted ValidatedInfo[(0)].", validatedInfo);
            LOGGER.error(msg);
            throw new NoPrincipalInfoException(msg);
        }

        return principalInfo;
    }

    @Override
    public void onLogout(PrincipalInfo principalInfo) {
        this.clearCache(principalInfo);
    }

    /**
     * 用户校验密码、校验短信验证码等
     * @author zifangsky
     * @date 2019/4/11 16:16
     * @since 1.0.0
     * @param validatedInfo 登录时来至外部的需要验证的信息
     * @param principalInfo 数据库中正确的用户主体信息
     * @throws AuthenticationException AuthenticationException
     */
    protected void checkCredentialsInfo(ValidatedInfo validatedInfo, PrincipalInfo principalInfo) throws AuthenticationException{
        //默认只实现“用户名+密码”模式的校验
        if(validatedInfo instanceof UsernamePasswordValidatedInfo){
            UsernamePasswordValidatedInfo usernamePasswordValidatedInfo = (UsernamePasswordValidatedInfo) validatedInfo;

            if(EncryptionTypeEnums.CUSTOM.equals(usernamePasswordValidatedInfo.getEncryptionType())){
                this.checkCustomUsernamePasswordValidatedInfo(usernamePasswordValidatedInfo, principalInfo);
            }else{
                boolean correct = EncryptionTypeEnums.verifyCredentials(usernamePasswordValidatedInfo.getEncryptionType(),
                        principalInfo.getPassword(), validatedInfo.getCredentials());

                //密码校验失败，则抛出异常
                if(!correct){
                    String msg = MessageFormat.format("Submitted credentials for validatedInfo[(0)] did not match the expected credentials.", validatedInfo);
                    throw new IncorrectCredentialsException(msg);
                }
            }
        }
    }

    /**
     * “用户名+密码”模式，校验自定义的密码加密方式
     * @author zifangsky
     * @date 2019/4/11 16:24
     * @since 1.0.0
     * @param validatedInfo 登录时来至外部的需要验证的信息
     * @param principalInfo 数据库中正确的用户主体信息
     * @throws AuthenticationException AuthenticationException
     */
    protected void checkCustomUsernamePasswordValidatedInfo(UsernamePasswordValidatedInfo validatedInfo, PrincipalInfo principalInfo) throws AuthenticationException{

    }

    /**
     * 获取缓存的用户实体信息
     */
    private PrincipalInfo getCachedPrincipalInfo(ValidatedInfo validatedInfo){
        if(enablePrincipalInfoCache && principalInfoCache != null){
            //获取缓存的KEY
            String cacheKey = this.getPrincipalInfoCacheKey(validatedInfo);
            if(cacheKey != null){
                PrincipalInfo result = principalInfoCache.get(principalInfoCacheName, cacheKey);
                LOGGER.debug(MessageFormat.format("get cached PrincipalInfo, key:[{0}], value:[{1}].", cacheKey, result));

                return result;
            }
        }

        return null;
    }

    /**
     * 尝试缓存用户实体信息
     */
    private void cachePrincipalInfo(ValidatedInfo validatedInfo, PrincipalInfo principalInfo){
        if(enablePrincipalInfoCache && principalInfoCache != null){
            //获取缓存的KEY
            String cacheKey = this.getPrincipalInfoCacheKey(validatedInfo);
            if(cacheKey != null){
                //缓存
                principalInfoCache.put(principalInfoCacheName, cacheKey, principalInfo);
            }
        }
    }

    protected String getPrincipalInfoCacheKey(PrincipalInfo principalInfo){
        return principalInfo.getAccount();
    }

    protected String getPrincipalInfoCacheKey(ValidatedInfo validatedInfo){
        return validatedInfo.getSubject();
    }

    /**
     * 清除缓存
     * @author zifangsky
     * @date 2019/4/11 11:47
     * @since 1.0.0
     * @param principalInfo principalInfo
     */
    protected void clearCache(PrincipalInfo principalInfo){
        if(principalInfo != null){
            LOGGER.debug(MessageFormat.format("Began to clear account [{0}] caches.", principalInfo));
            this.doClearCache(principalInfo);
        }
    }

    /**
     * 真正的清除缓存的逻辑
     * @author zifangsky
     * @date 2019/4/11 11:47
     * @since 1.0.0
     * @param principalInfo principalInfo
     */
    protected void doClearCache(PrincipalInfo principalInfo){
        this.clearCachedPrincipalInfo(principalInfo);
    }

    protected void clearCachedPrincipalInfo(PrincipalInfo principalInfo){
        if(principalInfo != null && enablePrincipalInfoCache && principalInfoCache != null){
            //获取缓存的KEY
            String cacheKey = this.getPrincipalInfoCacheKey(principalInfo);
            if(cacheKey != null){
                //清除缓存
                principalInfoCache.remove(principalInfoCacheName, cacheKey);
            }
        }
    }

    public boolean isEnablePrincipalInfoCache() {
        return enablePrincipalInfoCache;
    }

    public void setEnablePrincipalInfoCache(boolean enablePrincipalInfoCache) {
        this.enablePrincipalInfoCache = enablePrincipalInfoCache;
    }

    public Cache<String, PrincipalInfo> getPrincipalInfoCache() {
        return principalInfoCache;
    }

    public void setPrincipalInfoCache(Cache<String, PrincipalInfo> principalInfoCache) {
        this.principalInfoCache = principalInfoCache;
    }

    public String getPrincipalInfoCacheName() {
        return principalInfoCacheName;
    }

    public void setPrincipalInfoCacheName(String principalInfoCacheName) {
        this.principalInfoCacheName = principalInfoCacheName;
    }
}
