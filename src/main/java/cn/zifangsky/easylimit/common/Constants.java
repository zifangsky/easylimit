package cn.zifangsky.easylimit.common;

/**
 * 公共常量类
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public class Constants {
    /**
     * 项目名
     */
    public static final String PROJECT_NAME = "easylimit";

    /**
     * session校验的线程名
     */
    public static final String SESSION_VALIDATION_THREAD_NAME = "THREAD_SESSION_VALIDATION";

    /**
     * 默认sessionId在cookie中的名称
     */
    public static final String DEFAULT_COOKIE_SESSION_ID_NAME = PROJECT_NAME + "_session_id";

    /**
     * Ajax请求的Header
     */
    public static final String AJAX_REQUEST_HEADER = "XMLHttpRequest";

    /**
     * 保存的请求来源的URL的参数名称
     */
    public static final String SAVED_SOURCE_URL_NAME = "saved_source_url";

    /**
     * 默认的登录URL
     */
    public static final String DEFAULT_LOGIN_URL = "/login.html";

    /**
     * 默认的登录校验URL
     */
    public static final String DEFAULT_LOGIN_CHECK_URL = "/check";

    /**
     * 默认的未授权URL
     */
    public static final String DEFAULT_UNAUTHORIZED_URL = "/error.html";

    /**
     * 默认的注销之后的重定向URL
     */
    public static final String DEFAULT_LOGOUT_REDIRECT_URL = DEFAULT_LOGIN_URL;


}
