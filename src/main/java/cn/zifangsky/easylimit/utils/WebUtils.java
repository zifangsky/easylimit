package cn.zifangsky.easylimit.utils;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.common.SpringContextUtils;
import cn.zifangsky.easylimit.session.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Web请求响应相关的公共方法
 *
 * @author zifangsky
 * @date 2019/4/29
 * @since 1.0.0
 */
public class WebUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);

    /**
     * 默认编码
     */
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    /**
     * 获取完整的请求URL
     */
    public static String getCompleteRequestUrl() {
        return getCompleteRequestUrl(SpringContextUtils.getRequest());
    }

    /**
     * 获取完整的请求URL
     */
    public static String getCompleteRequestUrl(HttpServletRequest request) {
        //当前请求路径
        String currentUrl = request.getRequestURL().toString();
        //请求参数
        String queryString = request.getQueryString();
        if (!org.apache.commons.lang3.StringUtils.isEmpty(queryString)) {
            currentUrl = currentUrl + "?" + queryString;
        }

        return encodeRequestString(currentUrl, "UTF-8");
    }

    /**
     * 获取请求的客户端IP
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNoneBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 判断是否是Ajax请求
     */
    public static boolean isAjaxRequest(ServletRequest request) {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");

        return header != null && Constants.AJAX_REQUEST_HEADER.equalsIgnoreCase(header);
    }


    /**
     * 获取RequestURL
     * <p>
     *     假如请求URL为：<b>http://example.com:8080/demoapp/user/index?name=admin</b>，
     *     其中<code>demoapp</code>为部署的应用名称
     * </p>
     * <p>最后返回的字符串是：<b>http://example.com:8080/demoapp/user/index</b></p>
     * @author zifangsky
     * @date 2019/4/29 15:58
     * @since 1.0.0
     * @param request request
     * @return java.lang.String
     */
    public static String getRequestURL(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();

        return handlerFormat(decodeRequestString(request, requestURL));
    }

    /**
     * 获取完整URL
     * <p>
     *     假如请求URL为：<b>http://example.com:8080/demoapp/user/index?name=admin</b>，
     *     其中<code>demoapp</code>为部署的应用名称
     * </p>
     * <p>最后返回的字符串是：<b>http://example.com:8080/demoapp/user/index?name=admin</b></p>
     * @author zifangsky
     * @date 2020/5/31 10:24
     * @since 1.0.0
     * @param request request
     * @return java.lang.String
     */
    public static String getRequestFullURL(HttpServletRequest request) {
        String requestURL = getRequestURL(request);
        String queryString = request.getQueryString();

        if(queryString == null){
            return requestURL;
        }else {
            return requestURL + "?" + queryString;
        }
    }

    /**
     * 获取ServletPath
     * <p>
     *     假如请求URL为：<b>http://example.com:8080/demoapp/user/index?name=admin</b>，
     *     其中<code>demoapp</code>为部署的应用名称
     * </p>
     * <p>最后返回的字符串是：<b>/user/index</b></p>
     * @author zifangsky
     * @date 2019/4/29 15:58
     * @since 1.0.0
     * @param request request
     * @return java.lang.String
     */
    public static String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        return handlerFormat(decodeRequestString(request, servletPath));
    }

    /**
     * 获取RequestURI
     * <p>
     *     假如请求URL为：<b>http://example.com:8080/demoapp/user/index?name=admin</b>，
     *     其中<code>demoapp</code>为部署的应用名称
     * </p>
     * <p>最后返回的字符串是：<b>/demoapp/user/index</b></p>
     * @author zifangsky
     * @date 2019/4/29 15:58
     * @since 1.0.0
     * @param request request
     * @return java.lang.String
     */
    public static String getRequestURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return handlerFormat(decodeRequestString(request, requestURI));
    }

    /**
     * 获取ContextPath
     * <p>
     *     假如请求URL为：<b>http://example.com:8080/demoapp/user/index?name=admin</b>，
     *     其中<code>demoapp</code>为部署的应用名称
     * </p>
     * <p>最后返回的字符串是：<b>/demoapp</b></p>
     * @author zifangsky
     * @date 2019/4/29 15:58
     * @since 1.0.0
     * @param request request
     * @return java.lang.String
     */
    public static String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        contextPath = handlerFormat(decodeRequestString(request, contextPath));

        if ("/".equals(contextPath)) {
            contextPath = "";
        }

        return contextPath;
    }

    /**
     * 处理URL格式
     */
    public static String handlerFormat(String path) {
        return handlerFormat(path, true);
    }

    /**
     * 处理URL格式
     * @author zifangsky
     * @date 2019/4/29 15:37
     * @since 1.0.0
     * @param path PATH
     * @param replaceBackSlash 是否处理反斜杠（将 '\\' 替换成 '/'）
     * @return java.lang.String
     */
    private static String handlerFormat(String path, boolean replaceBackSlash) {
        if (path == null){
            return null;
        }

        String result = path;

        //1. 替换反斜杠
        if (replaceBackSlash && result.indexOf('\\') >= 0){
            result = result.replace('\\', '/');
        }

        if ("/.".equals(result)) {
            return "/";
        }

        //2. 给URL前面添加'/'
        if (!result.startsWith("http") && !result.startsWith("/")) {
            result = "/" + result;
        }

        //3 去除多余的'/'（“http://”或者“https://”不去除）
        result = result.replaceAll("//", "/");
        result = result.replaceFirst(":/", "://");

        //4. 去除'../'
        result = result.replaceAll("\\.\\./", "");

        //5. 去除'./'
        result = result.replaceAll("\\./", "");

        return result;
    }

    /**
     * 重定向
     * @author zifangsky
     * @date 2019/4/30 17:09
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param redirectUrl 重定向URL
     */
    public static void executeRedirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl) throws IOException {
        executeRedirect(request, response, redirectUrl, null);
    }

    /**
     * 重定向
     * @author zifangsky
     * @date 2019/4/30 17:09
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param redirectUrl 重定向URL
     * @param params 请求参数
     */
    public static void executeRedirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl, Map<String, String> params) throws IOException {
        executeRedirect(request, response, redirectUrl, params, true);
    }

    /**
     * 重定向
     * @author zifangsky
     * @date 2019/4/30 17:09
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param redirectUrl 重定向URL
     * @param params 请求参数
     * @param autoContextPath 是否自动设置ContextPath
     */
    public static void executeRedirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl, Map<String, String> params, boolean autoContextPath) throws IOException {
        if(redirectUrl == null){
            throw new IllegalArgumentException("Parameter redirectUrl cannot be empty.");
        }

        //获取可能存在的页面内锚定位
        String anchor = null;
        int anchorIndex = redirectUrl.indexOf('#');
        if (anchorIndex > -1) {
            anchor = redirectUrl.substring(anchorIndex);
            redirectUrl = redirectUrl.substring(0, anchorIndex);
        }

        StringBuilder targetUrl = new StringBuilder();

        //1. 添加ContextPath
        if(autoContextPath){
            targetUrl.append(getContextPath(request));
        }

        //2. 添加RedirectUrl
        if(!redirectUrl.startsWith("/")){
            redirectUrl = "/" + redirectUrl;
        }
        targetUrl.append(redirectUrl);

        //3. 拼接请求参数
        if(params != null && params.size() > 0){
            boolean first = (redirectUrl.indexOf('?') < 0);

            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first) {
                    targetUrl.append('?');
                    first = false;
                } else {
                    targetUrl.append('&');
                }

                String encodedKey = encodeRequestString(entry.getKey(), DEFAULT_CHARACTER_ENCODING);
                String encodedValue = (entry.getValue() != null ? encodeRequestString(entry.getValue(), DEFAULT_CHARACTER_ENCODING) : "");
                targetUrl.append(encodedKey).append('=').append(encodedValue);
            }
        }

        //4. 添加锚定位
        if(anchor != null){
            targetUrl.append(anchor);
        }

        //5. 重定向
        response.sendRedirect(response.encodeRedirectURL(targetUrl.toString()));
    }

    /**
     * 保存来源URL
     * @author zifangsky
     * @date 2019/5/7 16:49
     * @since 1.0.0
     * @param request HttpServletRequest
     */
    public static String saveSourceUrl(HttpServletRequest request){
        String requestFullURL = getRequestFullURL(request);

        Access access = SecurityUtils.getAccess();
        Session session = access.getSession();

        //在session中保存来源URL
        session.setAttribute(Constants.SAVED_SOURCE_URL_NAME, requestFullURL);

        return requestFullURL;
    }

    /**
     * 将{@link ServletRequest}转化为{@link HttpServletRequest}
     */
    public static HttpServletRequest toHttp(ServletRequest request){
        return (HttpServletRequest) request;
    }

    /**
     * 将{@link ServletResponse}转化为{@link HttpServletResponse}
     */
    public static HttpServletResponse toHttp(ServletResponse response){
        return (HttpServletResponse) response;
    }

    /**
     * 编码请求URL
     */
    public static String encodeRequestString(String source, String encoding) {
        try {
            return URLEncoder.encode(source, encoding);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(MessageFormat.format("Cannot encode request string [{0}] with encoding [{1}].", source, encoding));
        }

        return source;
    }

    /**
     * 解码请求URL
     */
    public static String decodeRequestString(HttpServletRequest request, String source) {
        String encoding = getEncoding(request);
        try {
            return URLDecoder.decode(source, encoding);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(MessageFormat.format("Cannot decode request string [{0}] with encoding [{1}].", source, encoding));
        }

        return source;
    }

    /**
     * 获取编码
     */
    private static String getEncoding(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();

        if (encoding == null) {
            encoding = DEFAULT_CHARACTER_ENCODING;
        }

        return encoding;
    }
}
