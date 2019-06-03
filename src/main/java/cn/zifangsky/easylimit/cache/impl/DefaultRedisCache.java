package cn.zifangsky.easylimit.cache.impl;

import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.exception.cache.CacheException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 默认的Redis缓存方式
 *
 * @author zifangsky
 * @date 2019/4/11
 * @since 1.0.0
 */
public class DefaultRedisCache implements Cache<Serializable, Object> {
    /**
     * 使用{@link RedisTemplate}操作所有{@link Cache}
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 使用{@link RedisTemplate}的HASH存储具体的{@link Cache}
     */
    private HashOperations<String, Serializable, Object> opsForHash;

    public DefaultRedisCache(RedisTemplate<String, Object> redisTemplate) {
        if(redisTemplate == null){
            throw new IllegalArgumentException("Parameter redisTemplate cannot be empty.");
        }

        this.redisTemplate = redisTemplate;
        this.opsForHash = redisTemplate.opsForHash();
    }

    @Override
    public Object get(String cacheName, Serializable key) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Parameter key cannot be empty.");
        }

        return opsForHash.get(cacheName, key.toString());
    }

    @Override
    public void put(String cacheName, Serializable key, Object value) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Parameter key cannot be empty.");
        }

        if(value != null){
            opsForHash.put(cacheName, key.toString(), value);
        }else{
            opsForHash.delete(cacheName, key.toString());
        }
    }

    @Override
    public void putAll(String cacheName, Map<Serializable, Object> sources) throws CacheException {
        if(sources != null && sources.size() > 0){
            opsForHash.putAll(cacheName, sources);
        }
    }

    @Override
    public void remove(String cacheName, Serializable key) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Parameter key cannot be empty.");
        }

        //数据Map
        opsForHash.delete(cacheName, key.toString());
    }

    @Override
    public void clear(String cacheName) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        this.redisTemplate.delete(cacheName);
    }

    @Override
    public int size(String cacheName) {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        return opsForHash.size(cacheName).intValue();
    }

    @Override
    public Set<Serializable> keySet(String cacheName) {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        return opsForHash.keys(cacheName);
    }

    @Override
    public Collection<Object> values(String cacheName) {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        return opsForHash.values(cacheName);
    }
}
