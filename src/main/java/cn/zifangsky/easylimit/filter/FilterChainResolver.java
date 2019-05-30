package cn.zifangsky.easylimit.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于获取代理{@link FilterChain}
 *
 * @author zifangsky
 * @date 2019/5/10
 * @since 1.0.0
 */
public interface FilterChainResolver {
    /**
     * 获取代理FilterChain
     * @author zifangsky
     * @date 2019/5/10 16:33
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param originalChain 原FilterChain
     * @return javax.servlet.FilterChain
     */
    FilterChain getProxiedFilterChain(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain);
}
