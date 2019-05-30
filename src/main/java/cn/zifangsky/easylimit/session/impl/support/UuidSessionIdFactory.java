package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.session.SessionIdFactory;

import java.io.Serializable;
import java.util.UUID;

/**
 * 通过{@link UUID}生成sessionId
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public class UuidSessionIdFactory implements SessionIdFactory {

    @Override
    public Serializable generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
