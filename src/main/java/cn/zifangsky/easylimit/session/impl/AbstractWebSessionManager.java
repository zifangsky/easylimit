package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.exception.session.UnknownSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.SessionDAO;
import cn.zifangsky.easylimit.session.SessionFactory;
import cn.zifangsky.easylimit.session.SessionIdFactory;
import cn.zifangsky.easylimit.session.SessionKey;
import cn.zifangsky.easylimit.session.SessionManager;
import cn.zifangsky.easylimit.session.impl.support.SnowFlakeSessionIdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Set;

/**
 * 基于Web的{@link SessionManager}
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public abstract class AbstractWebSessionManager extends AbstractValidationSessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWebSessionManager.class);

    /**
     * 用于生成sessionId
     */
    private SessionIdFactory sessionIdFactory;
    /**
     * 用于生成真正的session
     */
    private SessionFactory sessionFactory;
    /**
     * 用于存储session
     */
    private SessionDAO sessionDAO;
    /**
     * 是否删除不可用的session
     */
    private boolean deleteInvalidSessions;

    public AbstractWebSessionManager() {
        //默认使用雪花算法生成sessionId
        this.sessionIdFactory = new SnowFlakeSessionIdFactory(0, 0);
        //默认的SessionFactory
        this.sessionFactory = new DefaultSessionFactory();
        //默认使用内存存储session，且不缓存
        this.sessionDAO = new MemorySessionDAO();
        //默认删除过期不可用的session
        this.deleteInvalidSessions = true;
    }

    /**
     * 通过{@link ServletRequest}获取sessionId
     * @author zifangsky
     * @date 2019/4/9 0009 17:38
     * @since 1.0.0
     * @param request request
     * @param response response
     * @return java.io.Serializable
     */
    public abstract Serializable getSessionId(ServletRequest request, ServletResponse response);

    @Override
    protected Set<Session> getActiveSessions() {
        return sessionDAO.getActiveSessions();
    }

    @Override
    protected Session doCreateSession(SessionContext sessionContext) {
        //1. 创建sessionId
        Serializable sessionId = sessionIdFactory.generateSessionId();
        sessionContext.setSessionId(sessionId);

        //2. 创建session
        Session session = this.sessionFactory.createSession(sessionContext);
        LOGGER.debug(MessageFormat.format("The session instance has been created id [{0}].", session.getId()));

        //3. sessionDAO存储一下
        this.storeSession(session);

        return session;
    }

    @Override
    protected Session retrieveSession(SessionKey key) throws UnknownSessionException {
        return sessionDAO.read(key.getSessionId());
    }

    @Override
    protected void storeSession(Session session) {
        this.sessionDAO.update(session);
    }

    /**
     * 在{@link SessionDAO}中删除{@link Session}
     *
     * @param session session
     * @author zifangsky
     * @date 2019/4/2 14:31
     * @since 1.0.0
     */
    protected void deleteSession(Session session) {
        this.sessionDAO.delete(session);
    }

    @Override
    protected void afterStopped(Session session) {
        try {
            this.doStopped(session);
        }finally {
            if (deleteInvalidSessions) {
                this.deleteSession(session);
            }
        }
    }

    /**
     * 用于子类重写以便在删除session之前做点其他操作
     */
    protected void doStopped(Session session) {

    }

    @Override
    protected void afterExpired(Session session) {
        try {
            this.doExpired(session);
        }finally {
            if (deleteInvalidSessions) {
                this.deleteSession(session);
            }
        }
    }

    /**
     * 用于子类重写以便在删除session之前做点其他操作
     */
    protected void doExpired(Session session) {

    }

    public SessionIdFactory getSessionIdFactory() {
        return sessionIdFactory;
    }

    public void setSessionIdFactory(SessionIdFactory sessionIdFactory) {
        this.sessionIdFactory = sessionIdFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionDAO getSessionDAO() {
        return sessionDAO;
    }

    public void setSessionDAO(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    public boolean isDeleteInvalidSessions() {
        return deleteInvalidSessions;
    }

    public void setDeleteInvalidSessions(boolean deleteInvalidSessions) {
        this.deleteInvalidSessions = deleteInvalidSessions;
    }
}
