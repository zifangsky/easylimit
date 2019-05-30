package cn.zifangsky.easylimit.session;

import cn.zifangsky.easylimit.exception.session.InvalidSessionException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * Session
 *
 * @author zifangsky
 * @date 2019/3/23
 * @since 1.0.0
 */
public interface Session {

    /**
     * 获取sessionId
     *
     * @return java.io.Serializable
     * @author zifangsky
     * @date 2019/3/23 15:54
     * @since 1.0.0
     */
    Serializable getId();

    /**
     * 获取Host
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/3/23 15:54
     * @since 1.0.0
     */
    String getHost();

    /**
     * 获取{@link Session}的创建时间
     *
     * @return java.time.LocalDateTime
     * @author zifangsky
     * @date 2019/3/23 16:34
     * @since 1.0.0
     */
    LocalDateTime getCreateTime();

    /**
     * 获取{@link Session}的最新访问时间
     *
     * @return java.time.LocalDateTime
     * @author zifangsky
     * @date 2019/3/23 16:34
     * @since 1.0.0
     */
    LocalDateTime getLatestAccessTime();

    /**
     * 获取{@link Session}的停用时间
     *
     * @return java.time.LocalDateTime
     * @author zifangsky
     * @date 2019/3/23 16:34
     * @since 1.0.0
     */
    LocalDateTime getStopTime();

    /**
     * 获取设置的{@link Session}的超时时间
     *
     * @return long
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/23 16:39
     * @since 1.0.0
     */
    long getTimeout() throws InvalidSessionException;

    /**
     * 设置{@link Session}的超时时间
     *
     * @param maxIdleTime 最大的空闲时间（即：超时时间）
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/23 16:41
     * @since 1.0.0
     */
    void setTimeout(long maxIdleTime) throws InvalidSessionException;

    /**
     * 获取设置的{@link Session}的超时时间的单位
     *
     * @return java.util.concurrent.TimeUnit
     * @author zifangsky
     * @date 2019/3/23 16:42
     * @since 1.0.0
     */
    ChronoUnit getTimeoutChronoUnit();

    /**
     * 设置{@link Session}的超时时间的单位
     *
     * @param timeoutChronoUnit 超时时间的单位
     * @author zifangsky
     * @date 2019/3/23 16:43
     * @since 1.0.0
     */
    void setTimeoutChronoUnit(ChronoUnit timeoutChronoUnit);

    /**
     * 返回{@link Session}是否有效
     *
     * @return boolean
     * @author zifangsky
     * @date 2019/3/23 16:46
     * @since 1.0.0
     */
    boolean isValid();

    /**
     * 刷新{@link Session}的最新访问时间
     *
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/23 16:46
     * @since 1.0.0
     */
    void refresh() throws InvalidSessionException;

    /**
     * 验证{@link Session}是否可用
     *
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/23 16:48
     * @since 1.0.0
     */
    void validate() throws InvalidSessionException;

    /**
     * 停用{@link Session}
     *
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/25 15:59
     * @since 1.0.0
     */
    void stop() throws InvalidSessionException;

    /**
     * 返回所有保存在{@link Session}中的 KEY
     *
     * @return java.util.Collection<java.lang.String>
     * @throws InvalidSessionException InvalidSessionException
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/25 16:01
     * @since 1.0.0
     */
    Collection<String> getAttributeNames() throws InvalidSessionException;

    /**
     * 返回保存在{@link Session}中的指定键值对
     *
     * @param key KEY
     * @return java.lang.Object
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/25 16:03
     * @since 1.0.0
     */
    Object getAttribute(String key) throws InvalidSessionException;

    /**
     * 向{@link Session}中添加指定键值对
     *
     * @param key   KEY
     * @param value VALUE
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/25 16:05
     * @since 1.0.0
     */
    void setAttribute(String key, Object value) throws InvalidSessionException;

    /**
     * 从{@link Session}中移除某个键值对
     *
     * @param key KEY
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/25 16:10
     * @since 1.0.0
     */
    void removeAttribute(String key) throws InvalidSessionException;

}
