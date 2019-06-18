package cn.zifangsky.easylimit.enums;

import cn.zifangsky.easylimit.session.Session;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 系统中默认时间的枚举
 *
 * @author zifangsky
 * @date 2019/3/26
 * @since 1.0.0
 */
public enum DefaultTimeEnums {
    /**
     * {@link Session}的默认超时时间（120分钟）
     */
    SESSION_TIMEOUT(120, ChronoUnit.MINUTES, TimeUnit.MINUTES),
    /**
     * {@link Session}的默认校验频率（30分钟）
     */
    SESSION_VALIDATION(30, ChronoUnit.MINUTES, TimeUnit.MINUTES),
    /**
     * Access Token的默认有效期为1天
     */
    ACCESS_TOKEN(1, ChronoUnit.DAYS, TimeUnit.DAYS),
    /**
     * Refresh Token的默认有效期为180天
     */
    REFRESH_TOKEN(180, ChronoUnit.DAYS, TimeUnit.DAYS)
    ;

    private long time;

    private ChronoUnit chronoUnit;

    private TimeUnit timeUnit;

    DefaultTimeEnums(long time, ChronoUnit chronoUnit, TimeUnit timeUnit) {
        this.time = time;
        this.chronoUnit = chronoUnit;
        this.timeUnit = timeUnit;
    }

    public long getTime() {
        return time;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
