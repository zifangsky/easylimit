package cn.zifangsky.easylimit.session;

/**
 * {@link Session}监听器
 *
 * @author zifangsky
 * @date 2019/3/29
 * @since 1.0.0
 */
public interface SessionListener {
    /**
     * {@link Session}创建完成之后触发
     *
     * @author zifangsky
     * @date 2019/3/29 15:06
     * @since 1.0.0
     */
    void onCreate();

    /**
     * {@link Session}停止之后触发
     *
     * @author zifangsky
     * @date 2019/3/29 15:06
     * @since 1.0.0
     */
    void onStop();

    /**
     * {@link Session}过期之后触发
     *
     * @author zifangsky
     * @date 2019/3/29 15:06
     * @since 1.0.0
     */
    void onExpiration();
}
