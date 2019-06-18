package cn.zifangsky.easylimit.access.impl;

import cn.zifangsky.easylimit.TokenWebSecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.AccessContext;
import cn.zifangsky.easylimit.access.AccessFactory;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.impl.support.SimpleAccessToken;
import cn.zifangsky.easylimit.session.impl.support.SimpleRefreshToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 基于token模式的{@link AccessFactory}
 *
 * @author zifangsky
 * @date 2019/4/8
 * @since 1.0.0
 */
public class TokenAccessFactory implements AccessFactory {

    @Override
    public Access createAccess(AccessContext accessContext) {
        TokenAccessContext tokenAccessContext = (TokenAccessContext) accessContext;

        ServletRequest request = tokenAccessContext.acquireServletRequest();
        ServletResponse response = tokenAccessContext.acquireServletResponse();
        String host = tokenAccessContext.acquireHost();
        Session session = tokenAccessContext.acquireSession();
        boolean authenticated = tokenAccessContext.acquireAuthenticated();
        PrincipalInfo principalInfo = tokenAccessContext.acquirePrincipalInfo();
        TokenWebSecurityManager securityManager = (TokenWebSecurityManager) tokenAccessContext.acquireSecurityManager();
        SimpleAccessToken accessToken = tokenAccessContext.acquireAccessToken();
        SimpleRefreshToken refreshToken = tokenAccessContext.acquireRefreshToken();

        return new ExposedTokenAccess(request, response, host, session, authenticated, principalInfo, securityManager, accessToken, refreshToken);
    }
}
