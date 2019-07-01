package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.exception.session.InvalidSessionException;
import cn.zifangsky.easylimit.exception.session.SessionException;
import cn.zifangsky.easylimit.exception.session.UnknownSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.SessionKey;
import cn.zifangsky.easylimit.session.SessionListener;
import cn.zifangsky.easylimit.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 对{@link SessionManager}的基本扩充
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public abstract class AbstractSessionManager implements SessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSessionManager.class);

    /**
     * 全局的{@link Session}最大的空闲时间
     */
    private Long globalTimeout;
    /**
     * 全局的超时时间的单位
     */
    private ChronoUnit globalTimeoutChronoUnit;

    /**
     * {@link Session}监听器集合
     */
    private Set<SessionListener> sessionListeners;

    public AbstractSessionManager() {
    }

    public AbstractSessionManager(long globalTimeout, ChronoUnit globalTimeoutChronoUnit) {
        this.globalTimeout = globalTimeout;
        this.globalTimeoutChronoUnit = globalTimeoutChronoUnit;
    }

    public AbstractSessionManager(Long globalTimeout, ChronoUnit globalTimeoutChronoUnit, Set<SessionListener> sessionListeners) {
        this.globalTimeout = globalTimeout;
        this.globalTimeoutChronoUnit = globalTimeoutChronoUnit;
        this.sessionListeners = sessionListeners;
    }

    /**
     * 创建{@link Session}的具体逻辑
     *
     * @param sessionContext SessionContext
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/3/29 15:26
     * @since 1.0.0
     */
    protected abstract Session createSession(SessionContext sessionContext);

    /**
     * 获取真正的{@link Session}的逻辑
     *
     * @param key key
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/3/29 17:45
     * @since 1.0.0
     */
    protected abstract Session doGetSession(SessionKey key) throws InvalidSessionException;

    /**
     * 更新{@link Session}状态
     *
     * @param session session
     * @author zifangsky
     * @date 2019/3/29 15:29
     * @since 1.0.0
     */
    protected abstract void storeSession(Session session);

    /**
     * 允许子类在创建完{@link Session}后做一些其他操作
     *
     * @param session session
     * @param context context
     * @author zifangsky
     * @date 2019/3/29 15:36
     * @since 1.0.0
     */
    protected void afterCreate(Session session, SessionContext context) {
    }

    /**
     * {@link Session}被停止后需要做的其他操作
     */
    protected void afterStopped(Session session) {
    }

    @Override
    public Session getSession(SessionKey key) throws SessionException {
        if (key == null) {
            throw new IllegalArgumentException("Parameter sessionKey cannot be empty.");
        }

        Session session = (key.getSessionId() != null) ? this.doGetSession(key) : null;
        return session != null ? this.createExposedSession(session) : null;
    }

    @Override
    public Session create(SessionContext sessionContext) {
        //1. 创建session
        Session session = this.createSession(sessionContext);

        //2. 设置全局参数
        this.applyGlobalParameters(session);

        //3. 允许子类在创建完session后做一些其他操作
        this.afterCreate(session, sessionContext);

        //4. 通知监听器session已经创建
        this.notifyCreate(session);

        //5. 创建代理session并返回
        return this.createExposedSession(session, sessionContext);
    }

    protected Session createExposedSession(Session session) {
        SessionKey sessionKey = new DefaultSessionKey(session.getId());

        return new ExposedSession(sessionKey, this);
    }

    protected Session createExposedSession(Session session, SessionContext sessionContext) {
        SessionKey sessionKey = new DefaultSessionKey(session.getId());
        return new ExposedSession(sessionKey, this);
    }

    /**
     * 给{@link Session}设置全局参数
     *
     * @param session session
     * @author zifangsky
     * @date 2019/3/29 15:31
     * @since 1.0.0
     */
    protected void applyGlobalParameters(Session session) {
        if (globalTimeout != null && globalTimeoutChronoUnit != null) {
            session.setTimeout(this.globalTimeout);
            session.setTimeoutChronoUnit(this.globalTimeoutChronoUnit);
            //更新session状态
            this.storeSession(session);
        }
    }

    /**
     * 通知{@link Session}已经创建
     *
     * @param session session
     * @author zifangsky
     * @date 2019/3/29 15:40
     * @since 1.0.0
     */
    protected void notifyCreate(Session session) {
        if(this.sessionListeners != null && this.sessionListeners.size() > 0){
            sessionListeners.forEach(listener -> listener.onCreate(session));
        }
    }

    /**
     * 通知{@link Session}已经停止
     *
     * @param session session
     * @author zifangsky
     * @date 2019/3/29 15:40
     * @since 1.0.0
     */
    protected void notifyStop(Session session) {
        if(this.sessionListeners != null && this.sessionListeners.size() > 0){
            sessionListeners.forEach(listener -> listener.onStop(session));
        }
    }

    /**
     * 通知{@link Session}已经过期
     *
     * @param session session
     * @author zifangsky
     * @date 2019/3/29 15:40
     * @since 1.0.0
     */
    protected void notifyExpiration(Session session) {
        if(this.sessionListeners != null && this.sessionListeners.size() > 0){
            sessionListeners.forEach(listener -> listener.onExpiration(session));
        }
    }

    @Override
    public boolean checkExist(SessionKey key) {
        if (key == null || key.getSessionId() == null) {
            LOGGER.error("Parameter sessionKey cannot be empty.");
            return false;
        } else {
            try {
                Session session = this.getRealSession(key);
                return true;
            } catch (SessionException e) {
                LOGGER.error(MessageFormat.format("Session with SessionId[{0}] does not exist.", key.getSessionId()), e);
                return false;
            }
        }
    }

    @Override
    public String getHost(SessionKey key) {
        return this.getRealSession(key).getHost();
    }

    @Override
    public LocalDateTime getCreateTime(SessionKey key) {
        return this.getRealSession(key).getCreateTime();
    }

    @Override
    public LocalDateTime getLatestAccessTime(SessionKey key) {
        return this.getRealSession(key).getLatestAccessTime();
    }

    @Override
    public LocalDateTime getStopTime(SessionKey key) {
        return this.getRealSession(key).getStopTime();
    }

    @Override
    public long getTimeout(SessionKey key) throws InvalidSessionException {
        return this.getRealSession(key).getTimeout();
    }

    @Override
    public void setTimeout(SessionKey key, long maxIdleTime) throws InvalidSessionException {
        Session session = this.getRealSession(key);
        session.setTimeout(maxIdleTime);

        this.storeSession(session);
    }

    @Override
    public ChronoUnit getTimeoutChronoUnit(SessionKey key) {
        return this.getRealSession(key).getTimeoutChronoUnit();
    }

    @Override
    public void setTimeoutChronoUnit(SessionKey key, ChronoUnit timeoutChronoUnit) {
        Session session = this.getRealSession(key);
        session.setTimeoutChronoUnit(timeoutChronoUnit);

        this.storeSession(session);
    }

    @Override
    public boolean isValid(SessionKey key) {
        return this.getRealSession(key).isValid();
    }

    @Override
    public void validate(SessionKey key) throws InvalidSessionException {
        this.getRealSession(key).validate();
    }

    @Override
    public void refresh(SessionKey key) throws InvalidSessionException {
        Session session = this.getRealSession(key);
        session.refresh();

        this.storeSession(session);
    }

    @Override
    public void stop(SessionKey key) throws InvalidSessionException {
        Session session = this.getRealSession(key);
        try {
            LOGGER.info(MessageFormat.format("Stopping Session with key[{0}].", key));
            session.stop();
            this.storeSession(session);
            notifyStop(session);
        } finally {
            this.afterStopped(session);
        }
    }

    @Override
    public Collection<String> getAttributeNames(SessionKey key) throws InvalidSessionException {
        return this.getRealSession(key).getAttributeNames();
    }

    @Override
    public Object getAttribute(SessionKey key, String attributeKey) throws InvalidSessionException {
        return this.getRealSession(key).getAttribute(attributeKey);
    }

    @Override
    public void setAttribute(SessionKey key, String attributeKey, Object value) throws InvalidSessionException {
        Session session = this.getRealSession(key);
        session.setAttribute(attributeKey, value);

        this.storeSession(session);
    }

    @Override
    public void removeAttribute(SessionKey key, String attributeKey) throws InvalidSessionException {
        Session session = this.getRealSession(key);
        session.removeAttribute(attributeKey);

        this.storeSession(session);
    }

    /**
     * 通过SessionKey获取真正的{@link Session}
     */
    private Session getRealSession(SessionKey key) throws SessionException {
        if (key == null || key.getSessionId() == null) {
            throw new IllegalArgumentException("Parameter sessionKey cannot be empty.");
        }

        Session session = this.doGetSession(key);
        if (session == null) {
            throw new UnknownSessionException(MessageFormat.format("The specified Session cannot be retrieved by SessionKey[{0}].", key));
        }

        return session;
    }

    public Long getGlobalTimeout() {
        return globalTimeout;
    }

    public void setGlobalTimeout(Long globalTimeout) {
        this.globalTimeout = globalTimeout;
    }

    public ChronoUnit getGlobalTimeoutChronoUnit() {
        return globalTimeoutChronoUnit;
    }

    public void setGlobalTimeoutChronoUnit(ChronoUnit globalTimeoutChronoUnit) {
        this.globalTimeoutChronoUnit = globalTimeoutChronoUnit;
    }

    public Set<SessionListener> getSessionListeners() {
        return sessionListeners;
    }

    public void setSessionListeners(Set<SessionListener> sessionListeners) {
        this.sessionListeners = sessionListeners != null ? sessionListeners : new HashSet<>();
    }
}
