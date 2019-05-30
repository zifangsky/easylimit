package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.exception.session.UnknownSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionDAO;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现的缓存{@link SessionDAO}
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public class DefaultCacheSessionDAO extends AbstractCacheSessionDAO {
    /**
     * 使用{@link ConcurrentHashMap}存储所有本地{@link Session}
     */
    private ConcurrentHashMap<Serializable, Session> sessionStorageMap;

    public DefaultCacheSessionDAO(Cache<Serializable, Session> cache) {
        super(cache);
        this.sessionStorageMap = new ConcurrentHashMap<>();
    }

    public DefaultCacheSessionDAO(Cache<Serializable, Session> cache, String sessionCacheName) {
        super(cache, sessionCacheName);
        this.sessionStorageMap = new ConcurrentHashMap<>();
    }

    public DefaultCacheSessionDAO(Cache<Serializable, Session> cache, ConcurrentHashMap<Serializable, Session> sessionStorageMap) {
        super(cache);
        this.sessionStorageMap = sessionStorageMap;
    }

    @Override
    protected Session doRead(Serializable sessionId) throws UnknownSessionException {
        return sessionStorageMap.get(sessionId);
    }

    @Override
    protected void doUpdate(Serializable sessionId, Session session) throws UnknownSessionException {
        sessionStorageMap.put(sessionId, session);
    }

    @Override
    protected void doDelete(Session session) throws UnknownSessionException {
        sessionStorageMap.remove(session.getId());
    }
}
