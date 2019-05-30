package cn.zifangsky.easylimit.session;

import cn.zifangsky.easylimit.exception.session.InvalidSessionException;
import cn.zifangsky.easylimit.exception.session.SessionException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * 管理{@link Session}的创建、修改、删除等。
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public interface SessionManager {

    /**
     * 通过{@link SessionKey}获取{@link Session}
     *
     * @param key SessionKey
     * @return cn.zifangsky.easylimit.session.Session
     * @throws SessionException SessionException
     * @author zifangsky
     * @date 2019/3/29 11:49
     * @since 1.0.0
     */
    Session getSession(SessionKey key) throws SessionException;

    /**
     * 通过{@link SessionContext}创建{@link Session}
     *
     * @param sessionContext sessionContext
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/3/29 15:17
     * @since 1.0.0
     */
    Session create(SessionContext sessionContext);

    /**
     * 通过{@link SessionKey}检查某个{@link Session}是否存在
     *
     * @param key SessionKey
     * @return boolean
     * @author zifangsky
     * @date 2019/3/29 13:40
     * @since 1.0.0
     */
    boolean checkExist(SessionKey key);

    /**
     * 通过{@link SessionKey}获取Host
     *
     * @param key SessionKey
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/3/29 13:27
     * @since 1.0.0
     */
    String getHost(SessionKey key);

    /**
     * 通过{@link SessionKey}获取{@link Session}创建时间
     *
     * @param key SessionKey
     * @return java.time.LocalDateTime
     * @author zifangsky
     * @date 2019/3/29 13:27
     * @since 1.0.0
     */
    LocalDateTime getCreateTime(SessionKey key);

    /**
     * 通过{@link SessionKey}获取{@link Session}最新访问时间
     *
     * @param key SessionKey
     * @return java.time.LocalDateTime
     * @author zifangsky
     * @date 2019/3/29 13:27
     * @since 1.0.0
     */
    LocalDateTime getLatestAccessTime(SessionKey key);

    /**
     * 通过{@link SessionKey}获取{@link Session}停用时间
     *
     * @param key SessionKey
     * @return java.time.LocalDateTime
     * @author zifangsky
     * @date 2019/3/29 13:27
     * @since 1.0.0
     */
    LocalDateTime getStopTime(SessionKey key);

    /**
     * 获取设置的{@link Session}的超时时间
     *
     * @param key SessionKey
     * @return long
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 16:49
     * @since 1.0.0
     */
    long getTimeout(SessionKey key) throws InvalidSessionException;

    /**
     * 设置{@link Session}的超时时间
     *
     * @param key         SessionKey
     * @param maxIdleTime 最大的空闲时间
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 16:54
     * @since 1.0.0
     */
    void setTimeout(SessionKey key, long maxIdleTime) throws InvalidSessionException;

    /**
     * 获取超时时间的单位
     *
     * @param key SessionKey
     * @return java.time.temporal.ChronoUnit
     * @author zifangsky
     * @date 2019/3/29 16:51
     * @since 1.0.0
     */
    ChronoUnit getTimeoutChronoUnit(SessionKey key);

    /**
     * 设置{@link Session}的超时时间的单位
     *
     * @param key               SessionKey
     * @param timeoutChronoUnit 超时时间的单位
     * @author zifangsky
     * @date 2019/3/29 16:51
     * @since 1.0.0
     */
    void setTimeoutChronoUnit(SessionKey key, ChronoUnit timeoutChronoUnit);

    /**
     * 通过{@link SessionKey}判断{@link Session}是否可用
     *
     * @param key SessionKey
     * @return boolean
     * @author zifangsky
     * @date 2019/3/29 13:30
     * @since 1.0.0
     */
    boolean isValid(SessionKey key);

    /**
     * 验证{@link Session}是否可用
     *
     * @param key SessionKey
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:30
     * @since 1.0.0
     */
    void validate(SessionKey key) throws InvalidSessionException;

    /**
     * 通过{@link SessionKey}刷新{@link Session}的最新访问时间
     *
     * @param key SessionKey
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:31
     * @since 1.0.0
     */
    void refresh(SessionKey key) throws InvalidSessionException;

    /**
     * 通过{@link SessionKey}停用某个{@link Session}
     *
     * @param key SessionKey
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:31
     * @since 1.0.0
     */
    void stop(SessionKey key) throws InvalidSessionException;

    /**
     * 通过{@link SessionKey}获取所有保存在{@link Session}中的 KEY
     *
     * @param key SessionKey
     * @return java.util.Collection<java.lang.String>
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:33
     * @since 1.0.0
     */
    Collection<String> getAttributeNames(SessionKey key) throws InvalidSessionException;

    /**
     * 通过{@link SessionKey}获取某个保存在{@link Session}中的键值对
     *
     * @param key          SessionKey
     * @param attributeKey 参数KEY
     * @return java.lang.Object
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:35
     * @since 1.0.0
     */
    Object getAttribute(SessionKey key, String attributeKey) throws InvalidSessionException;

    /**
     * 通过{@link SessionKey}向{@link Session}中添加指定键值对
     *
     * @param key          SessionKey
     * @param attributeKey KEY
     * @param value        VALUE
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:36
     * @since 1.0.0
     */
    void setAttribute(SessionKey key, String attributeKey, Object value) throws InvalidSessionException;

    /**
     * 通过{@link SessionKey}从{@link Session}中移除某个键值对
     *
     * @param key          SessionKey
     * @param attributeKey KEY
     * @throws InvalidSessionException InvalidSessionException
     * @author zifangsky
     * @date 2019/3/29 13:37
     * @since 1.0.0
     */
    void removeAttribute(SessionKey key, String attributeKey) throws InvalidSessionException;
}
