package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.exception.session.UnknownSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionDAO;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于缓存的{@link SessionDAO}
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public abstract class AbstractCacheSessionDAO implements SessionDAO {
    /**
     * 默认的session缓存名称
     */
    private static final String DEFAULT_SESSION_CACHE_NAME = Constants.PROJECT_NAME + ":session_cache";

    /**
     * 缓存实例
     */
    private Cache<Serializable, Session> cache;

    /**
     * session缓存名称
     */
    private String sessionCacheName;

    public AbstractCacheSessionDAO(Cache<Serializable, Session> cache) {
        this(cache, DEFAULT_SESSION_CACHE_NAME);
    }

    public AbstractCacheSessionDAO(Cache<Serializable, Session> cache, String sessionCacheName) {
        if(cache == null){
            throw new IllegalArgumentException("Parameter cache cannot be empty.");
        }
        if(sessionCacheName == null){
            throw new IllegalArgumentException("Parameter sessionCacheName cannot be empty.");
        }

        this.cache = cache;
        this.sessionCacheName = sessionCacheName;
    }

    /**
     * 真正的获取{@link Session}的逻辑
     *
     * @param sessionId sessionId
     * @return cn.zifangsky.easylimit.session.Session
     * @throws UnknownSessionException UnknownSessionException
     * @author zifangsky
     * @date 2019/4/1 18:28
     * @since 1.0.0
     */
    protected abstract Session doRead(Serializable sessionId) throws UnknownSessionException;

    /**
     * 真正的更新{@link Session}的逻辑
     *
     * @param sessionId sessionId
     * @param session   session
     * @throws UnknownSessionException UnknownSessionException
     * @author zifangsky
     * @date 2019/4/1 18:28
     * @since 1.0.0
     */
    protected abstract void doUpdate(Serializable sessionId, Session session) throws UnknownSessionException;

    /**
     * 真正的删除{@link Session}的逻辑
     *
     * @param session session
     * @throws UnknownSessionException UnknownSessionException
     * @author zifangsky
     * @date 2019/4/1 18:28
     * @since 1.0.0
     */
    protected abstract void doDelete(Session session) throws UnknownSessionException;

    /**
     * 将{@link Session}添加到缓存
     *
     * @param sessionId sessionId
     * @param session   session
     * @author zifangsky
     * @date 2019/4/1 18:20
     * @since 1.0.0
     */
    protected void putCache(Serializable sessionId, Session session) {
        if (sessionId != null && session != null) {
            cache.put(this.sessionCacheName, sessionId, session);
        }
    }

    /**
     * 从缓存获取{@link Session}
     *
     * @param sessionId sessionId
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/4/1 18:22
     * @since 1.0.0
     */
    protected Session getCache(Serializable sessionId) {
        if (sessionId != null) {
            return cache.get(this.sessionCacheName, sessionId);
        } else {
            return null;
        }
    }

    /**
     * 从缓存中移除{@link Session}
     *
     * @param session session
     * @author zifangsky
     * @date 2019/4/1 18:25
     * @since 1.0.0
     */
    protected void removeCache(Session session) {
        if (session != null && session.getId() != null) {
            cache.remove(this.sessionCacheName, session.getId());
        }
    }

    @Override
    public Session read(Serializable sessionId) throws UnknownSessionException {
        if (sessionId == null) {
            throw new IllegalArgumentException("Parameter sessionId cannot be empty.");
        }

        //先从本地获取，如果没有则从缓存获取
        Session session = this.doRead(sessionId);
        if (session == null) {
            session = this.getCache(sessionId);

            //如果不为空，则存一份session到本地
            if(session != null){
                this.doUpdate(sessionId, session);
            }
        }

        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            throw new IllegalArgumentException("Parameter session cannot be empty.");
        }

        //先更新本地，再更新缓存中的数据
        this.doUpdate(session.getId(), session);

        if (session.isValid()) {
            this.putCache(session.getId(), session);
        } else {
            this.removeCache(session);
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Parameter session cannot be empty.");
        }

        //先本地删除，再删除缓存中的数据
        this.doDelete(session);
        this.removeCache(session);
    }

    @Override
    public Set<Session> getActiveSessions() {
        Collection<Session> values = cache.values(this.sessionCacheName);
        Set<Session> result = new HashSet<>();

        if (values != null && values.size() > 0) {
            result.addAll(values);
        }

        return result;
    }

    public Cache<Serializable, Session> getCache() {
        return cache;
    }

    public void setCache(Cache<Serializable, Session> cache) {
        this.cache = cache;
    }

    public String getSessionCacheName() {
        return sessionCacheName;
    }

    public void setSessionCacheName(String sessionCacheName) {
        this.sessionCacheName = sessionCacheName;
    }
}
