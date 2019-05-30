package cn.zifangsky.easylimit.session;

import java.io.Serializable;

/**
 * 用于创建sessionId
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public interface SessionIdFactory {

    /**
     * 生成sessionId
     *
     * @return java.io.Serializable
     * @author zifangsky
     * @date 2019/3/29 17:20
     * @since 1.0.0
     */
    Serializable generateSessionId();
}
