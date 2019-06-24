package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.session.TokenDAO;
import cn.zifangsky.easylimit.session.TokenOperateResolver;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现的缓存{@link TokenDAO}
 *
 * @author zifangsky
 * @date 2019/6/24
 * @since 1.0.0
 */
public class DefaultCacheTokenDAO extends AbstractCacheTokenDAO{

    /**
     * 使用{@link ConcurrentHashMap}存储所有{@link SimpleAccessToken}
     */
    private ConcurrentHashMap<String, SimpleAccessToken> accessTokenStorageMap;

    /**
     * 使用{@link ConcurrentHashMap}存储所有{@link SimpleRefreshToken}
     */
    private ConcurrentHashMap<String, SimpleRefreshToken> refreshTokenStorageMap;

    public DefaultCacheTokenDAO(Cache<String, Object> cache) {
        super(cache);
        this.accessTokenStorageMap = new ConcurrentHashMap<>();
        this.refreshTokenStorageMap = new ConcurrentHashMap<>();
    }

    public DefaultCacheTokenDAO(Cache<String, Object> cache, TokenOperateResolver tokenOperateResolver) {
        super(cache, tokenOperateResolver);
        this.accessTokenStorageMap = new ConcurrentHashMap<>();
        this.refreshTokenStorageMap = new ConcurrentHashMap<>();
    }

    public DefaultCacheTokenDAO(Cache<String, Object> cache, String tokenCacheName, TokenOperateResolver tokenOperateResolver) {
        super(cache, tokenCacheName, tokenOperateResolver);
        this.accessTokenStorageMap = new ConcurrentHashMap<>();
        this.refreshTokenStorageMap = new ConcurrentHashMap<>();
    }

    @Override
    protected SimpleAccessToken doReadAccessToken(String accessToken) {
        return this.accessTokenStorageMap.get(accessToken);
    }

    @Override
    protected SimpleRefreshToken doReadRefreshToken(String refreshToken) {
        return this.refreshTokenStorageMap.get(refreshToken);
    }

    @Override
    protected void doUpdateAccessToken(SimpleAccessToken simpleAccessToken) {
        this.accessTokenStorageMap.put(simpleAccessToken.getAccessToken(), simpleAccessToken);
    }

    @Override
    protected void doUpdateRefreshToken(SimpleRefreshToken simpleRefreshToken) {
        this.refreshTokenStorageMap.put(simpleRefreshToken.getRefreshToken(), simpleRefreshToken);
    }

    @Override
    protected void doDeleteAccessToken(String accessToken) {
        this.accessTokenStorageMap.remove(accessToken);
    }

    @Override
    protected void doDeleteRefreshToken(String refreshToken) {
        this.refreshTokenStorageMap.remove(refreshToken);
    }
}
