package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.exception.session.UnknownSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionDAO;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存存储的{@link SessionDAO}
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public class MemorySessionDAO implements SessionDAO {
    /**
     * 使用{@link ConcurrentHashMap}存储所有{@link Session}
     */
    private ConcurrentHashMap<Serializable, Session> sessionStorageMap;

    public MemorySessionDAO() {
        this.sessionStorageMap = new ConcurrentHashMap<>();
    }

    public MemorySessionDAO(ConcurrentHashMap<Serializable, Session> sessionStorageMap) {
        this.sessionStorageMap = sessionStorageMap;
    }

    @Override
    public Session read(Serializable sessionId) throws UnknownSessionException {
        if (sessionId == null) {
            throw new IllegalArgumentException("Parameter sessionId cannot be empty.");
        }

        return sessionStorageMap.get(sessionId);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            throw new IllegalArgumentException("Parameter session cannot be empty.");
        }

        sessionStorageMap.put(session.getId(), session);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Parameter session cannot be empty.");
        }

        sessionStorageMap.remove(session.getId());
    }

    @Override
    public Set<Session> getActiveSessions() {
        Collection<Session> values = sessionStorageMap.values();
        Set<Session> result = new HashSet<>();

        if (values != null && values.size() > 0) {
            result.addAll(values);
        }

        return result;
    }
}
