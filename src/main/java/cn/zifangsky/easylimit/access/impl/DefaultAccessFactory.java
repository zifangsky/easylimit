package cn.zifangsky.easylimit.access.impl;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.AccessContext;
import cn.zifangsky.easylimit.access.AccessFactory;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.session.Session;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 默认的{@link AccessFactory}
 *
 * @author zifangsky
 * @date 2019/4/8
 * @since 1.0.0
 */
public class DefaultAccessFactory implements AccessFactory {

    @Override
    public Access createAccess(AccessContext accessContext) {
        ServletRequest request = accessContext.acquireServletRequest();
        ServletResponse response = accessContext.acquireServletResponse();
        String host = accessContext.acquireHost();
        Session session = accessContext.acquireSession();
        boolean authenticated = accessContext.acquireAuthenticated();
        PrincipalInfo principalInfo = accessContext.acquirePrincipalInfo();
        SecurityManager securityManager = accessContext.acquireSecurityManager();

        return new ExposedAccess(request, response, host, session, authenticated, principalInfo, securityManager);
    }
}
