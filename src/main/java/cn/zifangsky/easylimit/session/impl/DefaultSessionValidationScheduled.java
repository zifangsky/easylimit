package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.DefaultTimeEnums;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionValidationScheduled;
import cn.zifangsky.easylimit.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 默认的{@link SessionValidationScheduled}
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public class DefaultSessionValidationScheduled implements SessionValidationScheduled, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSessionValidationScheduled.class);

    /**
     * 调用{@link AbstractValidationSessionManager#validateSessions()}方法执行最终的{@link Session}校验逻辑
     */
    private AbstractValidationSessionManager validationSessionManager;
    /**
     * 定时任务
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * 定时任务是否已经开始
     */
    private boolean enabled = false;

    /**
     * {@link Session}校验的时间间隔
     */
    private long sessionValidationInterval = DefaultTimeEnums.SESSION_VALIDATION.getTime();
    /**
     * {@link Session}校验的时间单位
     */
    private TimeUnit sessionValidationUnit = DefaultTimeEnums.SESSION_VALIDATION.getTimeUnit();


    public DefaultSessionValidationScheduled() {
    }

    public DefaultSessionValidationScheduled(AbstractValidationSessionManager validationSessionManager) {
        this.validationSessionManager = validationSessionManager;
    }

    public DefaultSessionValidationScheduled(AbstractValidationSessionManager validationSessionManager, long sessionValidationInterval, TimeUnit sessionValidationUnit) {
        this.validationSessionManager = validationSessionManager;
        this.sessionValidationInterval = sessionValidationInterval;
        this.sessionValidationUnit = sessionValidationUnit;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void startScheduled() {
        if (scheduledExecutorService == null) {
            //1. 定义一个1个线程的定时任务
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName(Constants.SESSION_VALIDATION_THREAD_NAME);

                    return thread;
                }
            });

            //2. 设置执行频率
            scheduledExecutorService.scheduleAtFixedRate(this,
                    sessionValidationInterval,
                    sessionValidationInterval,
                    sessionValidationUnit);

            //3. 标识任务已经开始
            this.enabled = true;
        }
    }

    @Override
    public void stopScheduled() {
        if (scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
        }
        this.enabled = false;
    }

    /**
     * 定时任务的执行逻辑
     *
     * @author zifangsky
     * @date 2019/4/1 13:35
     * @since 1.0.0
     */
    @Override
    public void run() {
        LOGGER.debug(MessageFormat.format("Start the session validation task at:[{0}].", DateUtils.nowStr()));

        this.validationSessionManager.performValidationTask();

        LOGGER.debug(MessageFormat.format("End the session validation task at:[{0}].", DateUtils.nowStr()));
    }
}
