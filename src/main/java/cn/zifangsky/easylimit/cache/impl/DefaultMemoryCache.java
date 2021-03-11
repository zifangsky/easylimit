package cn.zifangsky.easylimit.cache.impl;

import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.exception.cache.CacheException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的内存缓存方式
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public class DefaultMemoryCache implements Cache<Serializable, Object> {
    /**
     * 使用{@link ConcurrentHashMap}存储所有{@link Cache}
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<Serializable, Object>> memoryCacheMap;

    public DefaultMemoryCache() {
        this.memoryCacheMap = new ConcurrentHashMap<>(16);
    }

    public DefaultMemoryCache(ConcurrentHashMap<String, ConcurrentHashMap<Serializable, Object>> memoryCacheMap) {
        this.memoryCacheMap = memoryCacheMap;
    }

    @Override
    public Object get(String cacheName, Serializable key) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Parameter key cannot be empty.");
        }

        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);
        return dataMap != null ? dataMap.get(key) : null;
    }

    @Override
    public void put(String cacheName, Serializable key, Object value) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Parameter key cannot be empty.");
        }

        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = this.getMap(cacheName);

        if(value != null){
            dataMap.put(key, value);
        }else{
            dataMap.remove(key);
        }
    }

    @Override
    public void putAll(String cacheName, Map<Serializable, Object> sources) throws CacheException {
        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = this.getMap(cacheName);

        if(sources != null && sources.size() > 0){
            dataMap.putAll(sources);
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
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);
        if(dataMap != null){
            dataMap.remove(key);
        }
    }

    @Override
    public void clear(String cacheName) throws CacheException {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);
        if(dataMap != null){
            dataMap.clear();
        }
    }

    @Override
    public int size(String cacheName) {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);
        return dataMap != null ? dataMap.size() : 0;
    }

    @Override
    public Set<Serializable> keySet(String cacheName) {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);
        return dataMap != null ? dataMap.keySet() : Collections.emptySet();
    }

    @Override
    public Collection<Object> values(String cacheName) {
        if(cacheName == null){
            throw new IllegalArgumentException("Parameter cacheName cannot be empty.");
        }

        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);
        return dataMap != null ? dataMap.values() : Collections.emptyList();
    }

    /**
     * 根据cacheName获取缓存Map
     */
    private ConcurrentHashMap<Serializable, Object> getMap(String cacheName){
        //数据Map
        ConcurrentHashMap<Serializable, Object> dataMap = memoryCacheMap.get(cacheName);

        if(dataMap == null){
            dataMap = new ConcurrentHashMap<>(16);
            memoryCacheMap.put(cacheName, dataMap);
        }

        return dataMap;
    }
}
