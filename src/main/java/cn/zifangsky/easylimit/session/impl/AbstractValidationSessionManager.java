package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.exception.session.ExpiredSessionException;
import cn.zifangsky.easylimit.exception.session.InvalidSessionException;
import cn.zifangsky.easylimit.exception.session.UnknownSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.SessionKey;
import cn.zifangsky.easylimit.session.SessionListener;
import cn.zifangsky.easylimit.session.SessionValidationScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 添加定时任务校验{@link Session}的可用性
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public abstract class AbstractValidationSessionManager extends AbstractSessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractValidationSessionManager.class);

    /**
     * {@link Session}校验的定时任务
     */
    private SessionValidationScheduled sessionValidationScheduled;
    /**
     * {@link Session}校验的时间间隔
     */
    private Long sessionValidationInterval;
    /**
     * {@link Session}校验的时间单位
     */
    private TimeUnit sessionValidationUnit;

    public AbstractValidationSessionManager() {
    }

    public AbstractValidationSessionManager(long globalTimeout, ChronoUnit globalTimeoutChronoUnit) {
        super(globalTimeout, globalTimeoutChronoUnit);
    }

    public AbstractValidationSessionManager(Long globalTimeout, ChronoUnit globalTimeoutChronoUnit, Set<SessionListener> sessionListeners) {
        super(globalTimeout, globalTimeoutChronoUnit, sessionListeners);
    }

    /**
     * 获取所有可用状态的{@link Session}
     *
     * @return java.util.Set<cn.zifangsky.easylimit.session.Session>
     * @author zifangsky
     * @date 2019/4/1 15:09
     * @since 1.0.0
     */
    protected abstract Set<Session> getActiveSessions();

    /**
     * 真正的创建{@link Session}的逻辑
     *
     * @param sessionContext sessionContext
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/4/1 15:52
     * @since 1.0.0
     */
    protected abstract Session doCreateSession(SessionContext sessionContext);

    /**
     * 真正的获取{@link Session}的逻辑
     *
     * @param key key
     * @return cn.zifangsky.easylimit.session.Session
     * @throws UnknownSessionException UnknownSessionException
     * @author zifangsky
     * @date 2019/4/1 14:58
     * @since 1.0.0
     */
    protected abstract Session retrieveSession(SessionKey key) throws UnknownSessionException;

    @Override
    protected void storeSession(Session session) {
    }

    /**
     * 启用{@link Session}定时校验后需要做的其他操作
     *
     * @author zifangsky
     * @date 2019/4/1 14:37
     * @since 1.0.0
     */
    protected void afterSessionValidationEnabled() {
    }

    /**
     * {@link Session}过期处理完成之后的其他操作
     */
    protected void afterExpired(Session session) {
    }

    @Override
    protected Session createSession(SessionContext sessionContext) {
        this.enableSessionValidationIfNecessary();

        return this.doCreateSession(sessionContext);
    }

    @Override
    protected Session doGetSession(SessionKey key) throws InvalidSessionException {
        this.enableSessionValidationIfNecessary();

        Session session = this.retrieveSession(key);
        if (session != null) {
            this.validate(session, key);
        }

        return session;
    }

    /**
     * 校验{@link Session}的可用性，并处理不可用的{@link Session}
     *
     * @param session session
     * @param key     key
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/4/1 15:40
     * @since 1.0.0
     */
    protected void validate(Session session, SessionKey key) throws InvalidSessionException {
        try {
            session.validate();
        } catch (ExpiredSessionException e) {
            this.onExpiration(session, key);
            throw e;
        } catch (InvalidSessionException e) {
            this.onInvalidation(session, key, e);
            throw e;
        }
    }

    /**
     * {@link Session}过期的处理
     *
     * @param session session
     * @param key     key
     * @author zifangsky
     * @date 2019/4/1 15:22
     * @since 1.0.0
     */
    protected void onExpiration(Session session, SessionKey key) {
        LOGGER.info(MessageFormat.format("Session with id [{0}] has expired.", key));
        try {
            //1. 更新状态
            this.storeSession(session);
            //2. 通知已经过期
            this.notifyExpiration(session);
        } finally {
            //3. 过期之后的其他操作
            this.afterExpired(session);
        }
    }

    /**
     * {@link Session}不可用的处理
     *
     * @param session session
     * @param key     key
     * @author zifangsky
     * @date 2019/4/1 15:22
     * @since 1.0.0
     */
    protected void onInvalidation(Session session, SessionKey key, InvalidSessionException e) {
        //如果是过期的情况
        if (e instanceof ExpiredSessionException) {
            this.onExpiration(session, key);
            return;
        }

        //其他情况
        LOGGER.info(MessageFormat.format("Session with id [{0}] is invalid.", key));
        try {
            //1. 更新状态
            this.storeSession(session);
            //2. 通知已经停用
            this.notifyStop(session);
        } finally {
            //3. 停止之后的其他操作
            this.afterStopped(session);
        }
    }

    /**
     * 如果必要的话，启用{@link Session}定时校验
     */
    private void enableSessionValidationIfNecessary() {
        SessionValidationScheduled scheduled = this.sessionValidationScheduled;

        if (scheduled == null || !scheduled.isEnabled()) {
            this.enableSessionValidation();
        }
    }

    /**
     * 启用{@link Session}定时校验
     *
     * @author zifangsky
     * @date 2019/4/1 14:37
     * @since 1.0.0
     */
    protected synchronized void enableSessionValidation() {
        //1. 获取SessionValidationScheduled
        SessionValidationScheduled scheduled = this.sessionValidationScheduled;
        if (scheduled == null) {
            //如果不存在，则创建一个
            scheduled = this.createSessionValidationScheduled();
            this.sessionValidationScheduled = scheduled;
        }

        //2. 如果还没有启用定时任务，则开启定时任务
        if (!scheduled.isEnabled()) {
            scheduled.startScheduled();
            this.afterSessionValidationEnabled();

            LOGGER.debug("Enable session validating.");
        }
    }

    /**
     * 停用{@link Session}定时校验
     *
     * @author zifangsky
     * @date 2019/4/1 14:37
     * @since 1.0.0
     */
    protected synchronized void disableSessionValidation() {
        SessionValidationScheduled scheduled = this.sessionValidationScheduled;
        if (scheduled != null) {
            try {
                scheduled.stopScheduled();
                LOGGER.debug("Session validation has been disabled.");
            } catch (Exception e) {
                LOGGER.error("Session validation cannot be disabled.", e);
            }
        }
    }

    /**
     * 创建{@link SessionValidationScheduled}
     *
     * @return cn.zifangsky.easylimit.session.SessionValidationScheduled
     * @author zifangsky
     * @date 2019/4/1 14:23
     * @since 1.0.0
     */
    protected SessionValidationScheduled createSessionValidationScheduled() {
        DefaultSessionValidationScheduled scheduled;

        if (this.sessionValidationInterval != null && this.sessionValidationInterval > 0
                && this.sessionValidationUnit != null) {
            scheduled = new DefaultSessionValidationScheduled(this, sessionValidationInterval, sessionValidationUnit);
        } else {
            scheduled = new DefaultSessionValidationScheduled(this);
        }
        LOGGER.debug(MessageFormat.format("The default SessionValidationScheduled has been created, of type [{0}].", scheduled.getClass().getName()));
        return scheduled;
    }

    /**
     * 定时验证所有活动状态的{@link Session}，
     * 如果失效则需要更新其状态
     *
     * @author zifangsky
     * @date 2019/3/29 14:03
     * @since 1.0.0
     */
    public void validateSessions() {
        LOGGER.info("Start validating all active sessions.");
        //1. 获取所有可用状态的session
        Set<Session> activeSessions = this.getActiveSessions();

        if (activeSessions != null && activeSessions.size() > 0) {
            //2. 一个个地校验session的状态
            for (Session temp : activeSessions) {
                try {
                    this.validate(temp, new DefaultSessionKey(temp.getId()));
                } catch (InvalidSessionException e) {
                    String msg = MessageFormat.format("Session with id [{0}] is invalid.", temp.getId());
                    LOGGER.error(msg, e);
                }
            }
        }
    }


    public SessionValidationScheduled getSessionValidationScheduled() {
        return sessionValidationScheduled;
    }

    public void setSessionValidationScheduled(SessionValidationScheduled sessionValidationScheduled) {
        this.sessionValidationScheduled = sessionValidationScheduled;
    }

    public Long getSessionValidationInterval() {
        return sessionValidationInterval;
    }

    public void setSessionValidationInterval(Long sessionValidationInterval) {
        this.sessionValidationInterval = sessionValidationInterval;
    }

    public TimeUnit getSessionValidationUnit() {
        return sessionValidationUnit;
    }

    public void setSessionValidationUnit(TimeUnit sessionValidationUnit) {
        this.sessionValidationUnit = sessionValidationUnit;
    }
}
