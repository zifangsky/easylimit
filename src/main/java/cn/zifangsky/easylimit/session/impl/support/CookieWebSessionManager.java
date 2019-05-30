package cn.zifangsky.easylimit.session.impl.support;

import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.common.SpringContextUtils;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.session.SessionContext;
import cn.zifangsky.easylimit.session.impl.AbstractWebSessionManager;
import cn.zifangsky.easylimit.utils.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 使用cookie存储
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public class CookieWebSessionManager extends AbstractWebSessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieWebSessionManager.class);

    /**
     * 是否启用存储sessionId的cookie
     */
    private boolean enableSessionIdCookie;

    /**
     * sessionId的cookie基本配置
     */
    private CookieInfo sessionIdCookieInfo;

    public CookieWebSessionManager() {
        this(true);
    }

    public CookieWebSessionManager(CookieInfo cookieInfo) {
        if(cookieInfo == null){
            throw new IllegalArgumentException("Parameter access cannot be empty.");
        }
        this.enableSessionIdCookie = true;
        this.sessionIdCookieInfo = cookieInfo;
    }

    public CookieWebSessionManager(boolean enableSessionIdCookie) {
        this.enableSessionIdCookie = enableSessionIdCookie;

        if(enableSessionIdCookie){
            this.sessionIdCookieInfo = new CookieInfo(Constants.DEFAULT_COOKIE_SESSION_ID_NAME);
        }
    }

    @Override
    public Serializable getSessionId(ServletRequest request, ServletResponse response) {
        if(request != null && request instanceof HttpServletRequest){
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            return this.getCookieSessionId(httpServletRequest);
        }

        return null;
    }

    @Override
    protected void afterCreate(Session session, SessionContext context) {
        super.afterCreate(session, context);

        //1. 获取request和response
        HttpServletRequest request = SpringContextUtils.getRequest();
        HttpServletResponse response = SpringContextUtils.getResponse();

        //2. 在Cookie中保存sessionId
        if(this.enableSessionIdCookie){
            Serializable sessionId = session.getId();
            LOGGER.debug(MessageFormat.format("store SessionId to Cookie, cookieName:[{0}], sessionId:[{1}].", this.sessionIdCookieInfo.getName(), sessionId));
            this.storeCookieSessionId(sessionId, request, response);
        }
    }

    @Override
    protected void afterStopped(Session session) {
        super.afterStopped(session);

        //1. 获取request和response
        HttpServletRequest request = SpringContextUtils.getRequest();
        HttpServletResponse response = SpringContextUtils.getResponse();

        //2. 删除cookie中的sessionId
        if(this.enableSessionIdCookie){
            Serializable sessionId = session.getId();
            LOGGER.debug(MessageFormat.format("Session with id[{0}] has stopped, and cookieName[{1}] in cookie will be deleted next.", sessionId, this.sessionIdCookieInfo.getName()));
            this.removeCookieSessionId(request, response);
        }
    }

    @Override
    protected void afterExpired(Session session) {
        super.afterExpired(session);

        //1. 获取request和response
        HttpServletRequest request = SpringContextUtils.getRequest();
        HttpServletResponse response = SpringContextUtils.getResponse();

        //2. 删除cookie中的sessionId
        if(this.enableSessionIdCookie){
            Serializable sessionId = session.getId();
            LOGGER.debug(MessageFormat.format("Session with id[{0}] has expired, and cookieName[{1}] in cookie will be deleted next.", sessionId, this.sessionIdCookieInfo.getName()));
            this.removeCookieSessionId(request, response);
        }
    }

    /**
     * 从{@link javax.servlet.http.Cookie}获取sessionId
     * @author zifangsky
     * @date 2019/4/25 15:31
     * @since 1.0.0
     * @param request request
     * @return java.io.Serializable
     */
    private Serializable getCookieSessionId(HttpServletRequest request){
        //1. 从从Cookie获取sessionId
        String id = CookieUtils.getCookieValue(request, this.sessionIdCookieInfo.getName());

        //2. 如果获取不到，则记录一下
        if(id == null){
            LOGGER.debug(MessageFormat.format("Cannot get SessionId from Cookie, with name[{0}].", this.sessionIdCookieInfo.getName()));
        }

        return id;
    }

    /**
     * 在sessionId创建后保存到{@link javax.servlet.http.Cookie}
     * @author zifangsky
     * @date 2019/4/25 15:53
     * @since 1.0.0
     * @param sessionId 新创建的sessionId
     * @param request request
     * @param response response
     */
    private void storeCookieSessionId(Serializable sessionId, HttpServletRequest request, HttpServletResponse response){
        String cookieName = this.sessionIdCookieInfo.getName();
        String cookieValue = sessionId.toString();
        String domain = this.sessionIdCookieInfo.getDomain();
        boolean httpOnly = this.sessionIdCookieInfo.isHttpOnly();
        int maxAge = this.sessionIdCookieInfo.getMaxAge();
        String path = this.sessionIdCookieInfo.calculatePath(request);
        boolean secure = this.sessionIdCookieInfo.isSecure();

        //添加cookie
        CookieUtils.addCookie(response, cookieName, cookieValue, domain, httpOnly, maxAge, path, secure);
    }

    /**
     * 在sessionId创建后保存到{@link javax.servlet.http.Cookie}
     * @author zifangsky
     * @date 2019/4/25 16:00
     * @since 1.0.0
     * @param request request
     * @param response response
     */
    private void removeCookieSessionId(HttpServletRequest request, HttpServletResponse response){
        //删除cookie
        CookieUtils.delCookie(request, response, this.sessionIdCookieInfo.getName());
    }

    public boolean isEnableSessionIdCookie() {
        return enableSessionIdCookie;
    }

    public void setEnableSessionIdCookie(boolean enableSessionIdCookie) {
        this.enableSessionIdCookie = enableSessionIdCookie;
    }

    public CookieInfo getSessionIdCookieInfo() {
        return sessionIdCookieInfo;
    }

    public void setSessionIdCookieInfo(CookieInfo sessionIdCookieInfo) {
        this.sessionIdCookieInfo = sessionIdCookieInfo;
    }
}
