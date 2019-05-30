package cn.zifangsky.easylimit;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.AccessContext;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.realm.Realm;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.SessionKey;

/**
 * 认证、权限、session等管理的入口
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public interface SecurityManager extends Realm{

    /**
     * 登录认证
     *
     * @author zifangsky
     * @date 2019/4/3 16:25
     * @param access 当前的请求信息
     * @param validatedInfo 待验证的用户名、密码
     * @since 1.0.0
     * @throws AuthenticationException AuthenticationException
     */
    Access login(Access access, ValidatedInfo validatedInfo) throws AuthenticationException;

    /**
     * 注销登录
     *
     * @param access 当前的请求信息
     * @author zifangsky
     * @date 2019/4/3 16:29
     * @since 1.0.0
     */
    void logout(Access access);

    /**
     * 通过{@link AccessContext}创建{@link Access}
     * @author zifangsky
     * @date 2019/4/4 15:19
     * @since 1.0.0
     * @param accessContext accessContext
     * @return cn.zifangsky.easylimit.access.Access
     */
    Access createAccess(AccessContext accessContext);

    /**
     * 通过{@link SessionKey}获取{@link Session}
     *
     * @param key SessionKey
     * @param accessContext 当前请求的环境变量
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/3/29 11:49
     * @since 1.0.0
     */
    Session getSession(SessionKey key, AccessContext accessContext);

    /**
     * 通过{@link SessionContext}创建{@link Session}
     *
     * @param sessionContext sessionContext
     * @return cn.zifangsky.easylimit.session.Session
     * @author zifangsky
     * @date 2019/3/29 15:17
     * @since 1.0.0
     */
    Session createSession(SessionContext sessionContext);
}
