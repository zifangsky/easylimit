package cn.zifangsky.easylimit.access;

/**
 * {@link Access}的创建工厂
 *
 * @author zifangsky
 * @date 2019/4/8
 * @since 1.0.0
 */
public interface AccessFactory {

    /**
     * 通过{@link AccessContext}创建{@link Access}
     * @author zifangsky
     * @date 2019/4/8 11:02
     * @since 1.0.0
     * @param accessContext accessContext
     * @return cn.zifangsky.easylimit.access.Access
     */
    Access createAccess(AccessContext accessContext);
}
