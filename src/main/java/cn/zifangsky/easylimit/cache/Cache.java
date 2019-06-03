package cn.zifangsky.easylimit.cache;

import cn.zifangsky.easylimit.exception.cache.CacheException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 缓存的抽象方法
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public interface Cache<K, V> {
    /**
     * GET方法
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @param key 通过KEY获取VALUE
     * @return V
     * @throws CacheException CacheException
     * @author zifangsky
     * @date 2019/4/1 17:21
     * @since 1.0.0
     */
    V get(String cacheName, K key) throws CacheException;

    /**
     * PUT方法
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @param key   key
     * @param value value
     * @throws CacheException CacheException
     * @author zifangsky
     * @date 2019/4/1 17:21
     * @since 1.0.0
     */
    void put(String cacheName, K key, V value) throws CacheException;

    /**
     * PUT ALL方法
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @param sources  所有待保存的键值对
     * @throws CacheException CacheException
     * @author zifangsky
     * @date 2019/4/1 17:21
     * @since 1.0.0
     */
    void putAll(String cacheName, Map<K,V> sources) throws CacheException;

    /**
     * REMOVE方法
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @param key key
     * @throws CacheException CacheException
     * @author zifangsky
     * @date 2019/4/1 17:21
     * @since 1.0.0
     */
    void remove(String cacheName, K key) throws CacheException;

    /**
     * 清空缓存
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @throws CacheException CacheException
     * @author zifangsky
     * @date 2019/4/1 17:24
     * @since 1.0.0
     */
    void clear(String cacheName) throws CacheException;

    /**
     * 获取缓存的键值对的数量
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @return int
     * @author zifangsky
     * @date 2019/4/1 17:25
     * @since 1.0.0
     */
    int size(String cacheName);

    /**
     * 获取缓存的所有KEY
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @return java.util.Set<K>
     * @author zifangsky
     * @date 2019/4/1 17:25
     * @since 1.0.0
     */
    Set<K> keySet(String cacheName);

    /**
     * 获取缓存的所有VALUE
     *
     * @param cacheName 键值对所属的缓存集合名称
     * @return java.util.Collection<V>
     * @author zifangsky
     * @date 2019/4/1 17:27
     * @since 1.0.0
     */
    Collection<V> values(String cacheName);
}
