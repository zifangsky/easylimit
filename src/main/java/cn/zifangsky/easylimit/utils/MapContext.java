package cn.zifangsky.easylimit.utils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装饰针对{@link java.util.Map}的操作，实际数据存储在内部的{@link ConcurrentHashMap}中
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class MapContext implements Map<String, Object>, Serializable {
    private static final long serialVersionUID = 4888306869832322181L;

    /**
     * 真实存储数据的{@link java.util.Map}
     */
    private final Map<String, Object> chunkMap;

    public MapContext() {
        this.chunkMap = new ConcurrentHashMap<>();
    }

    public MapContext(Map<String, Object> map) {
        this();
        this.putAll(map);
    }

    @Override
    public int size() {
        return this.chunkMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.chunkMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.chunkMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.chunkMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.chunkMap.get(key);
    }

    public <E> E getByType(String key, Class<E> type) {
        Object result = this.get(key);

        if (result != null) {
            if (!type.isAssignableFrom(result.getClass())) {
                String msg = MessageFormat.format("Expected type was [{0}]," +
                        "but the real key of object is [{1}].", type.getName(), result.getClass().getName());
                throw new IllegalArgumentException(msg);
            } else {
                return (E) result;
            }
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        if (key != null && value != null) {
            return this.chunkMap.put(key, value);
        }

        return null;
    }

    @Override
    public Object remove(Object key) {
        return this.chunkMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        if (map != null && !map.isEmpty()) {
            this.chunkMap.putAll(map);
        }
    }

    @Override
    public void clear() {
        this.chunkMap.clear();
        ;
    }

    @Override
    public Set<String> keySet() {
        return this.chunkMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.chunkMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.chunkMap.entrySet();
    }
}
