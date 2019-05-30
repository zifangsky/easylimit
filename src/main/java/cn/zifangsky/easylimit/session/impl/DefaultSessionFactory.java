package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.SessionFactory;

import java.io.Serializable;

/**
 * 默认的{@link SessionFactory}
 *
 * @author zifangsky
 * @date 2019/3/26
 * @see SessionFactory
 * @since 1.0.0
 */
public class DefaultSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext sessionContext) {
        if (sessionContext != null) {
            String host = sessionContext.getHost();
            Serializable sessionId = sessionContext.getSessionId();

            if (host != null && sessionId != null) {
                return new SimpleSession(sessionId, host);
            }
        }

        return new SimpleSession();
    }
}
