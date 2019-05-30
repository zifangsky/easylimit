package cn.zifangsky.easylimit.filter;

import cn.zifangsky.easylimit.utils.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@link Filter}的基本实现
 *
 * @author zifangsky
 * @date 2019/4/29
 * @since 1.0.0
 */
public abstract class AbstractOncePerRequestFilter extends AbstractFilter{
    /**
     * 标识当前{@link Filter}是否已经被执行的参数后缀
     * @see #getHasFilteredAttributeName
     */
    public static final String FILTERED_ATTRIBUTE_SUFFIX = ":filtered";

    /**
     * 真正的过滤逻辑
     * @author zifangsky
     * @date 2019/4/29 14:00
     * @since 1.0.0
     * @param request request
     * @param response response
     * @param filterChain 过滤链
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    protected abstract void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException;

    /**
     * 获取“已经过滤”标识的参数名
     * @author zifangsky
     * @date 2019/4/29 13:45
     * @since 1.0.0
     * @return java.lang.String
     */
    protected String getHasFilteredAttributeName() {
        String filterName = getFilterName();

        if (filterName == null) {
            filterName = getClass().getName();
        }

        return filterName + FILTERED_ATTRIBUTE_SUFFIX;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("AbstractOncePerRequestFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);

        String hasFilteredAttributeName = getHasFilteredAttributeName();
        //获取是否已经执行的标识
        boolean hasFiltered = (request.getAttribute(hasFilteredAttributeName) != null);

        if (hasFiltered || shouldNotFilter(httpRequest)) {
            //继续执行后面的过滤链
            filterChain.doFilter(request, response);
        }
        else {
            //1. 标识已经执行
            request.setAttribute(hasFilteredAttributeName, Boolean.TRUE);
            try {
                //2. 执行过滤逻辑
                doFilterInternal(httpRequest, httpResponse, filterChain);
            }
            finally {
                //3. 执行完毕后移除标识
                request.removeAttribute(hasFilteredAttributeName);
            }
        }
    }

    /**
     * 不执行过滤（主要用于被子类覆盖）
     * @author zifangsky
     * @date 2019/4/29 13:57
     * @since 1.0.0
     * @param request request
     * @return boolean
     */
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }
}
