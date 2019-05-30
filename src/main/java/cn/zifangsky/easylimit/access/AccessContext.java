package cn.zifangsky.easylimit.access;

import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.utils.SecurityUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Map;

/**
 * 用于存储{@link Access}的初始数据，即：某次请求的环境变量
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public interface AccessContext extends Map<String, Object> {

    /**
     * 获取Host
     * @author zifangsky
     * @date 2019/4/4 13:33
     * @since 1.0.0
     * @return java.lang.String
     */
    String getHost();

    /**
     * 设置Host
     * @author zifangsky
     * @date 2019/4/4 13:33
     * @since 1.0.0
     * @param host host
     */
    void setHost(String host);

    /**
     * 获取{@link ServletRequest}
     * @author zifangsky
     * @date 2019/4/4 14:06
     * @since 1.0.0
     * @return javax.servlet.ServletRequest
     */
    ServletRequest getServletRequest();

    /**
     * 设置{@link ServletRequest}
     * @author zifangsky
     * @date 2019/4/4 14:07
     * @since 1.0.0
     * @param request request
     */
    void setServletRequest(ServletRequest request);

    /**
     * 获取{@link ServletResponse}
     * @author zifangsky
     * @date 2019/4/4 14:09
     * @since 1.0.0
     * @return javax.servlet.ServletResponse
     */
    ServletResponse getServletResponse();

    /**
     * 设置{@link ServletResponse}
     * @author zifangsky
     * @date 2019/4/4 14:09
     * @since 1.0.0
     * @param response response
     */
    void setServletResponse(ServletResponse response);

    /**
     * 获取sessionId
     * @author zifangsky
     * @date 2019/4/4 13:28
     * @since 1.0.0
     * @return java.io.Serializable
     */
    Serializable getSessionId();

    /**
     * 设置sessionId
     * @author zifangsky
     * @date 2019/4/4 13:29
     * @since 1.0.0
     * @param sessionId sessionId
     */
    void setSessionId(Serializable sessionId);

    /**
     * 获取当前请求的{@link Session}
     * @author zifangsky
     * @date 2019/4/4 13:30
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.session.Session
     */
    Session getSession();

    /**
     * 设置当前请求的{@link Session}
     * @author zifangsky
     * @date 2019/4/4 13:31
     * @since 1.0.0
     * @param session session
     */
    void setSession(Session session);

    /**
     * 获取当前登录用户主体
     * @author zifangsky
     * @date 2019/4/4 13:35
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.authc.Principal
     */
    PrincipalInfo getPrincipalInfo();

    /**
     * 设置当前登录用户主体
     * @author zifangsky
     * @date 2019/4/4 13:37
     * @since 1.0.0
     * @param principalInfo 当前登录用户主体
     */
    <T> void setPrincipalInfo(PrincipalInfo principalInfo);

    /**
     * 获取{@link SecurityManager}
     * @author zifangsky
     * @date 2019/4/4 13:58
     * @since 1.0.0
     * @return java.lang.SecurityManager
     */
    SecurityManager getSecurityManager();

    /**
     * 设置{@link SecurityManager}
     * @author zifangsky
     * @date 2019/4/4 14:00
     * @since 1.0.0
     * @param securityManager securityManager
     */
    void setSecurityManager(SecurityManager securityManager);

    /**
     * 返回是否已经登录验证通过
     * @author zifangsky
     * @date 2019/4/4 14:01
     * @since 1.0.0
     * @return boolean
     */
    Boolean isAuthenticated();

    /**
     * 设置是否已经登录验证通过
     * @author zifangsky
     * @date 2019/4/4 14:03
     * @since 1.0.0
     * @param authc 登录验证通过的标识
     */
    void setAuthenticated(boolean authc);

    /**
     * 获取{@link ServletRequest}中的Host
     * @author zifangsky
     * @date 2019/4/4 14:10
     * @since 1.0.0
     * @return java.lang.String
     */
    String acquireHost();

    /**
     * 获取{@link Access}中的session
     * @author zifangsky
     * @date 2019/4/4 14:14
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.session.Session
     */
    Session acquireSession();

    /**
     * 获取{@link Access}或者{@link Session}中的PrincipalInfo
     * @author zifangsky
     * @date 2019/4/4 14:18
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.authc.PrincipalInfo
     */
    PrincipalInfo acquirePrincipalInfo();

    /**
     * 获取{@link Session}中的登录状态
     * @author zifangsky
     * @date 2019/4/4 14:21
     * @since 1.0.0
     * @return boolean
     */
    Boolean acquireAuthenticated();

    /**
     * 通过{@link SecurityUtils} 获取{@link SecurityManager}
     * @author zifangsky
     * @date 2019/4/4 14:27
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.SecurityManager
     */
    SecurityManager acquireSecurityManager();

    /**
     * 获取{@link Access}中的{@link ServletRequest}
     * @author zifangsky
     * @date 2019/4/4 14:06
     * @since 1.0.0
     * @return javax.servlet.ServletRequest
     */
    ServletRequest acquireServletRequest();

    /**
     * 获取{@link Access}中的{@link ServletResponse}
     * @author zifangsky
     * @date 2019/4/4 14:09
     * @since 1.0.0
     * @return javax.servlet.ServletResponse
     */
    ServletResponse acquireServletResponse();


}
