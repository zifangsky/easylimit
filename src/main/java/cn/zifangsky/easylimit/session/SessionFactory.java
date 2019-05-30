package cn.zifangsky.easylimit.session;

/**
 * {@link Session}的创建工厂
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public interface SessionFactory {

    /**
     * 通过{@link SessionContext}创建{@link Session}
     *
     * @param sessionContext {@link Session}需要的基础数据
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/3/26 15:11
     * @since 1.0.0
     */
    Session createSession(SessionContext sessionContext);

}
