package cn.zifangsky.easylimit.session;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 用于存储{@link Session}的初始数据
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public interface SessionContext {

    /**
     * 获取Host
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/3/25 17:34
     * @since 1.0.0
     */
    String getHost();

    /**
     * 设置Host
     *
     * @param host host
     * @author zifangsky
     * @date 2019/3/25 17:34
     * @since 1.0.0
     */
    void setHost(String host);

    /**
     * 获取SessionId
     *
     * @return java.io.Serializable
     * @author zifangsky
     * @date 2019/3/25 17:35
     * @since 1.0.0
     */
    Serializable getSessionId();

    /**
     * 设置SessionId
     *
     * @param sessionId sessionId
     * @author zifangsky
     * @date 2019/3/25 17:35
     * @since 1.0.0
     */
    void setSessionId(Serializable sessionId);

    /**
     * 获取{@link ServletRequest}
     *
     * @return javax.servlet.ServletRequest
     * @author zifangsky
     * @date 2019/3/26 12:29
     * @since 1.0.0
     */
    ServletRequest getServletRequest();

    /**
     * 设置{@link ServletRequest}
     *
     * @param request ServletRequest
     * @author zifangsky
     * @date 2019/3/26 12:29
     * @since 1.0.0
     */
    void setServletRequest(ServletRequest request);

    /**
     * 获取{@link ServletResponse}
     *
     * @return javax.servlet.ServletResponse
     * @author zifangsky
     * @date 2019/3/26 12:30
     * @since 1.0.0
     */
    ServletResponse getServletResponse();

    /**
     * 设置{@link ServletResponse}
     *
     * @param response ServletResponse
     * @author zifangsky
     * @date 2019/3/26 12:30
     * @since 1.0.0
     */
    void setServletResponse(ServletResponse response);
}
