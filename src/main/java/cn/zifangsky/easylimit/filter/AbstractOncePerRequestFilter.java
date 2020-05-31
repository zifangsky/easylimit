package cn.zifangsky.easylimit.filter;

import cn.zifangsky.easylimit.filter.impl.support.TokenRespMsg;
import cn.zifangsky.easylimit.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Filter}的基本实现
 *
 * @author zifangsky
 * @date 2019/4/29
 * @since 1.0.0
 */
public abstract class AbstractOncePerRequestFilter extends AbstractFilter{
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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("AbstractOncePerRequestFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);

        if (shouldNotFilter(httpRequest)) {
            //继续执行后面的过滤链
            filterChain.doFilter(request, response);
        } else {
            //执行当前filter的过滤逻辑
            doFilterInternal(httpRequest, httpResponse, filterChain);
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

    /**
     * 组装基于Token请求的返回
     */
    protected void generateTokenResponse(HttpServletResponse response, TokenRespMsg tokenRespMsg) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        Map<String,Object> result = new HashMap<>(2);
        result.put("code", tokenRespMsg.getCode());
        result.put("name", tokenRespMsg.getName());
        result.put("msg", tokenRespMsg.getMsg());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
