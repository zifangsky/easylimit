package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.session.SessionKey;

import java.io.Serializable;

/**
 * 默认的{@link SessionKey}
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public class DefaultSessionKey implements SessionKey, Serializable {
    private static final long serialVersionUID = 5447080081332456113L;

    /**
     * SessionId
     */
    private Serializable sessionId;

    public DefaultSessionKey() {
    }

    public DefaultSessionKey(Serializable sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public Serializable getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(Serializable sessionId) {
        this.sessionId = sessionId;
    }
}
