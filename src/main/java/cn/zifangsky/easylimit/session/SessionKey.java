package cn.zifangsky.easylimit.session;

import java.io.Serializable;

/**
 * {@link Session}的Key
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public interface SessionKey {
    /**
     * 获取SessionId
     *
     * @return java.io.Serializable
     * @author zifangsky
     * @date 2019/3/29 11:40
     * @since 1.0.0
     */
    Serializable getSessionId();
}
