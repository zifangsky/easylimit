package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.enums.DefaultTimeEnums;
import cn.zifangsky.easylimit.exception.session.ExpiredSessionException;
import cn.zifangsky.easylimit.exception.session.InvalidSessionException;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Session}的基本实现
 *
 * @author zifangsky
 * @date 2019/3/26
 * @see Session
 * @since 1.0.0
 */
public class SimpleSession implements Session, Serializable {
    private static final long serialVersionUID = 254862408717602578L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSession.class);

    /**
     * SessionId
     */
    private Serializable sessionId;
    /**
     * 创建时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 最新访问时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestAccessTime;
    /**
     * 停用时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;
    /**
     * 最大的空闲时间（即：超时时间），默认为120分钟
     */
    private long timeout = DefaultTimeEnums.SESSION_TIMEOUT.getTime();

    /**
     * 超时时间的单位，默认单位为分钟
     */
    private ChronoUnit timeoutChronoUnit = DefaultTimeEnums.SESSION_TIMEOUT.getChronoUnit();

    /**
     * 是否已经过期
     */
    private boolean expired;
    /**
     * host
     */
    private String host;
    /**
     * {@link Session}中的所有参数
     */
    private Map<String, Object> attributes;

    public SimpleSession() {
        this.createTime = DateUtils.now();
        this.latestAccessTime = this.createTime;
        attributes = new HashMap<>();
    }

    public SimpleSession(String host) {
        this();
        this.host = host;
    }

    public SimpleSession(Serializable sessionId, String host) {
        this(host);
        this.sessionId = sessionId;
    }

    /**
     * 是否已经超时
     */
    private boolean isTimedOut() {
        if (isExpired()) {
            return true;
        }

        if (this.timeout > 0L) {
            LocalDateTime now = DateUtils.now();
            LocalDateTime expiredTime = latestAccessTime.plus(timeout, timeoutChronoUnit);

            return expiredTime.isBefore(now);
        } else {
            LOGGER.debug(MessageFormat.format("No timeout for session with id [{0}].", this.sessionId));
        }

        return false;
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        return !this.isStopped() && !this.isExpired();
    }

    @Override
    public void refresh() throws InvalidSessionException {
        this.latestAccessTime = DateUtils.now();
    }

    @Override
    public void validate() throws InvalidSessionException {
        if (this.isStopped()) {
            String msg = MessageFormat.format("Session with id [{0}] has stopped.", sessionId);
            LOGGER.info(msg);
            throw new InvalidSessionException(msg);
        } else if (this.isTimedOut()) {
            String msg = MessageFormat.format("Session with id [{0}] has expired. " +
                            "Last access time:{1}, current time:{2}, the session timeout has been set to {3} of {4}.",
                    sessionId, DateUtils.formatLocalDateTime(latestAccessTime), DateUtils.nowStr()
                    , timeout, timeoutChronoUnit.toString());
            LOGGER.info(msg);
            throw new ExpiredSessionException(msg);
        }
    }

    @Override
    public void stop() throws InvalidSessionException {
        if (this.stopTime == null) {
            this.stopTime = DateUtils.now();
        }
    }

    /**
     * 是否已经停用
     */
    private boolean isStopped() {
        return this.stopTime != null;
    }

    /**
     * 过期处理
     */
    public void expire() {
        this.stop();
        this.expired = true;
    }

    @Override
    public Serializable getId() {
        return this.sessionId;
    }

    public void setId(Serializable id) {
        this.sessionId = id;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public LocalDateTime getLatestAccessTime() {
        return this.latestAccessTime;
    }

    public void setLatestAccessTime(LocalDateTime latestAccessTime) {
        this.latestAccessTime = latestAccessTime;
    }

    @Override
    public LocalDateTime getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public long getTimeout() throws InvalidSessionException {
        return this.timeout;
    }

    @Override
    public void setTimeout(long maxIdleTime) throws InvalidSessionException {
        this.timeout = maxIdleTime;
    }

    @Override
    public ChronoUnit getTimeoutChronoUnit() {
        return this.timeoutChronoUnit;
    }

    @Override
    public void setTimeoutChronoUnit(ChronoUnit timeoutChronoUnit) {
        this.timeoutChronoUnit = timeoutChronoUnit;
    }

    @Override
    @JsonIgnore
    public Collection<String> getAttributeNames() throws InvalidSessionException {
        return attributes.keySet();
    }

    @Override
    @JsonIgnore
    public Object getAttribute(String key) throws InvalidSessionException {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) throws InvalidSessionException {
        if (value == null) {
            removeAttribute(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) throws InvalidSessionException {
        attributes.remove(key);
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
