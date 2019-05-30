package cn.zifangsky.easylimit.session;

/**
 * 校验{@link Session}可用性的定时任务
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public interface SessionValidationScheduled {
    /**
     * 当前定时任务是否可用
     */
    boolean isEnabled();

    /**
     * 开始定时任务
     */
    void startScheduled();

    /**
     * 停止定时任务
     */
    void stopScheduled();


}
