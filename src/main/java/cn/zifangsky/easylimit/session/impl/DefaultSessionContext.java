package cn.zifangsky.easylimit.session.impl;

import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.utils.MapContext;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Map;

/**
 * 默认的{@link SessionContext}
 *
 * @author zifangsky
 * @date 2019/3/25
 * @see SessionContext
 * @since 1.0.0
 */
public class DefaultSessionContext extends MapContext implements SessionContext {
    private static final long serialVersionUID = -4981375721508050679L;

    /**
     * host的key
     */
    private static final String HOST_KEY = DefaultSessionContext.class.getName() + ":host";

    /**
     * sessionId的key
     */
    private static final String SESSION_ID_KEY = DefaultSessionContext.class.getName() + ":session_id";

    /**
     * {@link ServletRequest}的key
     */
    private static final String SERVLET_REQUEST_KEY = DefaultSessionContext.class.getName() + ":servlet_request";

    /**
     * {@link ServletResponse}的key
     */
    private static final String SERVLET_RESPONSE_KEY = DefaultSessionContext.class.getName() + ":servlet_response";

    public DefaultSessionContext() {
        this(null, null);
    }

    public DefaultSessionContext(String host) {
        this(host, null);
    }

    public DefaultSessionContext(String host, Serializable sessionId) {
        super();

        if(host != null){
            this.setHost(host);
        }
        if(sessionId != null){
            this.setSessionId(sessionId);
        }
    }

    public DefaultSessionContext(Map<String, Object> map) {
        super(map);
    }


    @Override
    public String getHost() {
        return getByType(HOST_KEY, String.class);
    }

    @Override
    public void setHost(String host) {
        put(HOST_KEY, host);
    }

    @Override
    public Serializable getSessionId() {
        return getByType(SESSION_ID_KEY, Serializable.class);
    }

    @Override
    public void setSessionId(Serializable sessionId) {
        put(SESSION_ID_KEY, sessionId);
    }

    @Override
    public ServletRequest getServletRequest() {
        return getByType(SERVLET_REQUEST_KEY, ServletRequest.class);
    }

    @Override
    public void setServletRequest(ServletRequest request) {
        put(SERVLET_REQUEST_KEY, request);
    }

    @Override
    public ServletResponse getServletResponse() {
        return getByType(SERVLET_RESPONSE_KEY, ServletResponse.class);
    }

    @Override
    public void setServletResponse(ServletResponse response) {
        put(SERVLET_RESPONSE_KEY, response);
    }
}
