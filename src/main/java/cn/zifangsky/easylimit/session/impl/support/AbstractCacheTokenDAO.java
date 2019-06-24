package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.session.TokenDAO;
import cn.zifangsky.easylimit.session.TokenOperateResolver;
import cn.zifangsky.easylimit.session.impl.DefaultTokenOperateResolver;
import cn.zifangsky.easylimit.utils.DateUtils;

import java.util.Collection;

/**
 * 基于缓存的{@link TokenDAO}
 *
 * @author zifangsky
 * @date 2019/6/24
 * @since 1.0.0
 */
public abstract class AbstractCacheTokenDAO implements TokenDAO {
    /**
     * 默认的token缓存名称
     */
    private static final String DEFAULT_TOKEN_CACHE_NAME = Constants.PROJECT_NAME + ":token_cache";

    /**
     * 缓存实例
     */
    private Cache<String, Object> cache;

    /**
     * token缓存名称
     */
    private String tokenCacheName;

    /**
     * Access Token的缓存名称
     */
    private String accessTokenCacheName;

    /**
     * Refresh Token的缓存名称
     */
    private String refreshTokenCacheName;

    /**
     * Token操作类，用于校验Token是否过期
     */
    private TokenOperateResolver tokenOperateResolver;

    public AbstractCacheTokenDAO(Cache<String, Object> cache) {
        this(cache, DEFAULT_TOKEN_CACHE_NAME, new DefaultTokenOperateResolver());
    }

    public AbstractCacheTokenDAO(Cache<String, Object> cache, TokenOperateResolver tokenOperateResolver) {
        this(cache, DEFAULT_TOKEN_CACHE_NAME, tokenOperateResolver);
    }

    public AbstractCacheTokenDAO(Cache<String, Object> cache, String tokenCacheName, TokenOperateResolver tokenOperateResolver) {
        if(cache == null){
            throw new IllegalArgumentException("Parameter cache cannot be empty.");
        }
        if(tokenCacheName == null){
            throw new IllegalArgumentException("Parameter tokenCacheName cannot be empty.");
        }
        if(tokenOperateResolver == null){
            throw new IllegalArgumentException("Parameter tokenOperateResolver cannot be empty.");
        }

        this.cache = cache;
        this.tokenCacheName = tokenCacheName;
        this.accessTokenCacheName = this.generateAccessTokenCacheName(tokenCacheName);
        this.refreshTokenCacheName = this.generateRefreshTokenCacheName(tokenCacheName);
        this.tokenOperateResolver = tokenOperateResolver;
    }

    /**
     * 使用tokenCacheName生成Access Token的缓存名称
     */
    protected String generateAccessTokenCacheName(String tokenCacheName){
        return tokenCacheName + ":access_token";
    }

    /**
     * 使用tokenCacheName生成Refresh Token的缓存名称
     */
    protected String generateRefreshTokenCacheName(String tokenCacheName){
        return tokenCacheName + ":refresh_token";
    }

    /**
     * 从本地获取{@link SimpleAccessToken}的逻辑
     * @author zifangsky
     * @date 2019/6/24 14:40
     * @since 1.0.0
     * @param accessToken Access Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    protected abstract SimpleAccessToken doReadAccessToken(String accessToken);

    /**
     * 从本地获取{@link SimpleRefreshToken}的逻辑
     * @author zifangsky
     * @date 2019/6/24 14:40
     * @since 1.0.0
     * @param refreshToken Refresh Token
     * @return cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken
     */
    protected abstract SimpleRefreshToken doReadRefreshToken(String refreshToken);

    /**
     * 本地更新{@link SimpleAccessToken}的逻辑
     * @author zifangsky
     * @date 2019/6/24 14:42
     * @since 1.0.0
     * @param simpleAccessToken Access Token实例
     */
    protected abstract void doUpdateAccessToken(SimpleAccessToken simpleAccessToken);

    /**
     * 本地更新{@link SimpleRefreshToken}的逻辑
     * @author zifangsky
     * @date 2019/6/24 14:42
     * @since 1.0.0
     * @param simpleRefreshToken Refresh Token实例
     */
    protected abstract void doUpdateRefreshToken(SimpleRefreshToken simpleRefreshToken);

    /**
     * 本地删除{@link SimpleAccessToken}的逻辑
     * @author zifangsky
     * @date 2019/6/24 14:44
     * @since 1.0.0
     * @param accessToken Access Token
     */
    protected abstract void doDeleteAccessToken(String accessToken);

    /**
     * 本地删除{@link SimpleRefreshToken}的逻辑
     * @author zifangsky
     * @date 2019/6/24 14:44
     * @since 1.0.0
     * @param refreshToken Refresh Token
     */
    protected abstract void doDeleteRefreshToken(String refreshToken);

    /**
     * 将{@link SimpleAccessToken}添加到缓存
     * @author zifangsky
     * @date 2019/6/24 14:50
     * @since 1.0.0
     * @param accessToken Access Token
     */
    protected void putCache(SimpleAccessToken accessToken) {
        if (accessToken != null) {
            cache.put(this.accessTokenCacheName, accessToken.getAccessToken(), accessToken);
        }
    }

    /**
     * 将{@link SimpleRefreshToken}添加到缓存
     * @author zifangsky
     * @date 2019/6/24 14:50
     * @since 1.0.0
     * @param refreshToken Refresh Token
     */
    protected void putCache(SimpleRefreshToken refreshToken) {
        if (refreshToken != null) {
            cache.put(this.refreshTokenCacheName, refreshToken.getRefreshToken(), refreshToken);
        }
    }

    /**
     * 从缓存获取{@link SimpleAccessToken}或者{@link SimpleRefreshToken}
     * @author zifangsky
     * @date 2019/6/24 14:54
     * @since 1.0.0
     * @param token Access Token 或者 Refresh Token
     * @return java.lang.Object
     */
    protected Object getCache(String token, String cacheName) {
        if (token != null) {
            return cache.get(cacheName, token);
        } else {
            return null;
        }
    }

    /**
     * 从缓存中移除{@link SimpleAccessToken}或者{@link SimpleRefreshToken}
     * @author zifangsky
     * @date 2019/6/24 14:55
     * @since 1.0.0
     * @param token Access Token 或者 Refresh Token
     */
    protected void removeCache(String token, String cacheName) {
        if (token != null) {
            cache.remove(cacheName, token);
        }
    }


    @Override
    public synchronized SimpleAccessToken readByAccessToken(String accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("Parameter accessToken cannot be empty.");
        }

        //先从本地获取，如果没有则从缓存获取
        SimpleAccessToken simpleAccessToken = this.doReadAccessToken(accessToken);
        if (simpleAccessToken == null) {
            simpleAccessToken = (SimpleAccessToken) this.getCache(accessToken, this.accessTokenCacheName);

            //如果不为空，则存一份到本地
            if(simpleAccessToken != null){
                this.doUpdateAccessToken(simpleAccessToken);
            }
        }

        return simpleAccessToken;
    }

    @Override
    public synchronized SimpleRefreshToken readByRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Parameter refreshToken cannot be empty.");
        }

        //先从本地获取，如果没有则从缓存获取
        SimpleRefreshToken simpleRefreshToken = this.doReadRefreshToken(refreshToken);
        if (simpleRefreshToken == null) {
            simpleRefreshToken = (SimpleRefreshToken) this.getCache(refreshToken, this.refreshTokenCacheName);

            //如果不为空，则存一份到本地
            if(simpleRefreshToken != null){
                this.doUpdateRefreshToken(simpleRefreshToken);
            }
        }

        return simpleRefreshToken;
    }

    @Override
    public synchronized void updateAccessToken(SimpleAccessToken simpleAccessToken) {
        if (simpleAccessToken == null || simpleAccessToken.getAccessToken() == null) {
            throw new IllegalArgumentException("Parameter simpleAccessToken cannot be empty.");
        }

        //设置最新访问时间
        simpleAccessToken.setLatestAccessTime(DateUtils.now());

        //先更新本地，再更新缓存中的数据
        if (this.tokenOperateResolver.isValid(simpleAccessToken)) {
            this.doUpdateAccessToken(simpleAccessToken);
            this.putCache(simpleAccessToken);
        } else {
            this.deleteAccessToken(simpleAccessToken.getAccessToken());
        }
    }

    @Override
    public synchronized void updateRefreshToken(SimpleRefreshToken refreshToken) {
        if (refreshToken == null  || refreshToken.getRefreshToken() == null) {
            throw new IllegalArgumentException("Parameter refreshToken cannot be empty.");
        }

        //设置最新访问时间
        refreshToken.setLatestAccessTime(DateUtils.now());

        //先更新本地，再更新缓存中的数据
        if (this.tokenOperateResolver.isValid(refreshToken)) {
            this.doUpdateRefreshToken(refreshToken);
            this.putCache(refreshToken);
        } else {
            this.deleteRefreshToken(refreshToken.getRefreshToken());
        }
    }

    @Override
    public synchronized void deleteAccessToken(String accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("Parameter accessToken cannot be empty.");
        }

        //先本地删除，再删除缓存中的数据
        this.doDeleteAccessToken(accessToken);
        this.removeCache(accessToken, this.accessTokenCacheName);
    }

    @Override
    public synchronized void deleteRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Parameter refreshToken cannot be empty.");
        }

        //先本地删除，再删除缓存中的数据
        this.doDeleteAccessToken(refreshToken);
        this.removeCache(refreshToken, this.refreshTokenCacheName);
    }

    @Override
    public void deleteOldAccessToken(String account) {
        if(account != null){
            //1. 获取所有的Access Token
            Collection<Object> values = cache.values(this.accessTokenCacheName);

            if(values != null && values.size() > 0){
                for(Object o : values){
                    SimpleAccessToken accessToken = (SimpleAccessToken) o;

                    PrincipalInfo principalInfo = accessToken.getPrincipalInfo();
                    //2. 删除该用户的历史Access Token
                    if(principalInfo != null && account.equals(principalInfo.getAccount())){
                        this.deleteAccessToken(accessToken.getAccessToken());
                    }
                }
            }
        }
    }

    @Override
    public void deleteOldRefreshToken(String accessToken) {
        if(accessToken != null){
            //1. 获取所有的Refresh Token
            Collection<Object> values = cache.values(this.refreshTokenCacheName);

            if(values != null && values.size() > 0){
                for(Object o : values){
                    SimpleRefreshToken refreshToken = (SimpleRefreshToken) o;

                    //2. 删除某个Access Token关联的所有Refresh Token
                    if(accessToken.equals(refreshToken.getAccessToken())){
                        this.deleteRefreshToken(refreshToken.getRefreshToken());
                    }
                }
            }
        }
    }

    public Cache<String, Object> getCache() {
        return cache;
    }

    public void setCache(Cache<String, Object> cache) {
        this.cache = cache;
    }

    public String getTokenCacheName() {
        return tokenCacheName;
    }

    public void setTokenCacheName(String tokenCacheName) {
        this.tokenCacheName = tokenCacheName;
    }

    public TokenOperateResolver getTokenOperateResolver() {
        return tokenOperateResolver;
    }

    public void setTokenOperateResolver(TokenOperateResolver tokenOperateResolver) {
        this.tokenOperateResolver = tokenOperateResolver;
    }
}
