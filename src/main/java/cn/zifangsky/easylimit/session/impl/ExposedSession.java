package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.exception.session.InvalidSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionKey;
import cn.zifangsky.easylimit.session.SessionManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * 对外暴露的{@link Session}
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public class ExposedSession implements Session, Serializable {
    private static final long serialVersionUID = -8554129137035197796L;

    /**
     * 用于获取sessionId
     */
    private final SessionKey sessionKey;

    /**
     * 将对{@link Session}的操作委托给{@link SessionManager}
     */
    private final SessionManager sessionManager;

    public ExposedSession(SessionKey sessionKey, SessionManager sessionManager) {
        if (sessionKey == null) {
            throw new IllegalArgumentException("Parameter sessionKey cannot be empty.");
        }
        if (sessionManager == null) {
            throw new IllegalArgumentException("Parameter sessionManager cannot be empty.");
        }
        if (sessionKey.getSessionId() == null) {
            throw new IllegalArgumentException("The sessionId parameter in sessionManager cannot be empty.");
        }

        this.sessionKey = sessionKey;
        this.sessionManager = sessionManager;
    }

    @Override
    public Serializable getId() {
        return sessionKey.getSessionId();
    }

    @Override
    public String getHost() {
        return sessionManager.getHost(sessionKey);
    }

    @Override
    public LocalDateTime getCreateTime() {
        return sessionManager.getCreateTime(sessionKey);
    }

    @Override
    public LocalDateTime getLatestAccessTime() {
        return sessionManager.getLatestAccessTime(sessionKey);
    }

    @Override
    public LocalDateTime getStopTime() {
        return sessionManager.getStopTime(sessionKey);
    }

    @Override
    public long getTimeout() throws InvalidSessionException {
        return sessionManager.getTimeout(sessionKey);
    }

    @Override
    public void setTimeout(long maxIdleTime) throws InvalidSessionException {
        sessionManager.setTimeout(sessionKey, maxIdleTime);
    }

    @Override
    public ChronoUnit getTimeoutChronoUnit() {
        return sessionManager.getTimeoutChronoUnit(sessionKey);
    }

    @Override
    public void setTimeoutChronoUnit(ChronoUnit timeoutChronoUnit) {
        sessionManager.setTimeoutChronoUnit(sessionKey, timeoutChronoUnit);
    }

    @Override
    public boolean isValid() {
        return sessionManager.isValid(sessionKey);
    }

    @Override
    public void refresh() throws InvalidSessionException {
        sessionManager.refresh(sessionKey);
    }

    @Override
    public void validate() throws InvalidSessionException {
        sessionManager.validate(sessionKey);
    }

    @Override
    public void stop() throws InvalidSessionException {
        sessionManager.stop(sessionKey);
    }

    @Override
    public Collection<String> getAttributeNames() throws InvalidSessionException {
        return sessionManager.getAttributeNames(sessionKey);
    }

    @Override
    public Object getAttribute(String key) throws InvalidSessionException {
        return sessionManager.getAttribute(sessionKey, key);
    }

    @Override
    public void setAttribute(String key, Object value) throws InvalidSessionException {
        sessionManager.setAttribute(sessionKey, key, value);
    }

    @Override
    public void removeAttribute(String key) throws InvalidSessionException {
        sessionManager.removeAttribute(sessionKey, key);
    }
}
