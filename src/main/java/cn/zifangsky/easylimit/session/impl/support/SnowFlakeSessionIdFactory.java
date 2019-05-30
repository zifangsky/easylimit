package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.session.SessionIdFactory;
import cn.zifangsky.easylimit.utils.SnowFlake;

import java.io.Serializable;

/**
 * 基于雪花算法生成sessionId
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public class SnowFlakeSessionIdFactory implements SessionIdFactory {
    /**
     * 数据中心
     */
    private long datacenterId;
    /**
     * 机器标识
     */
    private long machineId;
    /**
     * 雪花算法
     */
    private SnowFlake snowFlake;

    public SnowFlakeSessionIdFactory(long datacenterId, long machineId) {
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        this.snowFlake = new SnowFlake(datacenterId, machineId);
    }

    @Override
    public Serializable generateSessionId() {
        return String.valueOf(snowFlake.nextId());
    }

    public long getDatacenterId() {
        return datacenterId;
    }


    public long getMachineId() {
        return machineId;
    }

}
