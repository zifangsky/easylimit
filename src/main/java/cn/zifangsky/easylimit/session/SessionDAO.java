package cn.zifangsky.easylimit.session;

import cn.zifangsky.easylimit.exception.session.UnknownSessionException;

import java.io.Serializable;
import java.util.Set;

/**
 * {@link Session}的存储
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public interface SessionDAO {

    /**
     * 通过sessionId查询{@link Session}
     *
     * @param sessionId sessionId
     * @return cn.zifangsky.easylimit.session.Session
     * @throws UnknownSessionException UnknownSessionException
     * @author zifangsky
     * @date 2019/4/1 16:51
     * @since 1.0.0
     */
    Session read(Serializable sessionId) throws UnknownSessionException;

    /**
     * 更新{@link Session}
     *
     * @param session session
     * @throws UnknownSessionException UnknownSessionException
     * @author zifangsky
     * @date 2019/4/1 16:52
     * @since 1.0.0
     */
    void update(Session session) throws UnknownSessionException;

    /**
     * 删除{@link Session}
     *
     * @param session session
     * @author zifangsky
     * @date 2019/4/1 16:52
     * @since 1.0.0
     */
    void delete(Session session);

    /**
     * 获取所有可用状态的{@link Session}
     *
     * @return java.util.Set<cn.zifangsky.easylimit.session.Session>
     * @author zifangsky
     * @date 2019/4/1 16:55
     * @since 1.0.0
     */
    Set<Session> getActiveSessions();
}
