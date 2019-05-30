package cn.zifangsky.easylimit.common;

/**
 * 在某些逻辑执行之前做一些绑定操作
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public interface BindOperator {
    /**
     * 绑定操作
     * @author zifangsky
     * @date 2019/4/4 17:14
     * @since 1.0.0
     */
    void bind();

    /**
     * 恢复线程中的ThreadLocal数据状态
     * @author zifangsky
     * @date 2019/4/26 11:21
     * @since 1.0.0
     */
    void recovery();
}
