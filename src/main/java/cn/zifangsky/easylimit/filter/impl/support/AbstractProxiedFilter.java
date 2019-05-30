package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.filter.AbstractOncePerRequestFilter;
import cn.zifangsky.easylimit.filter.FilterChainResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 所有自定义filter的执行入口
 *
 * @author zifangsky
 * @date 2019/5/15
 * @since 1.0.0
 */
public abstract class AbstractProxiedFilter extends AbstractOncePerRequestFilter{
    /**
     * securityManager
     */
    private SecurityManager securityManager;

    /**
     * filterChainManager
     */
    private FilterChainResolver filterChainResolver;

    public AbstractProxiedFilter(SecurityManager securityManager, FilterChainResolver filterChainResolver) {
        if (securityManager == null){
            throw new IllegalArgumentException("Parameter securityManager cannot be empty.");
        }
        if (filterChainResolver == null){
            throw new IllegalArgumentException("Parameter filterChainResolver cannot be empty.");
        }

        this.securityManager = securityManager;
        this.filterChainResolver = filterChainResolver;
    }

    protected void executeFilterChain(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain) throws ServletException, IOException {
        //1. 获取代理之后的FilterChain
        FilterChain filterChain = this.filterChainResolver.getProxiedFilterChain(request, response, originalChain);

        if(filterChain == null){
            filterChain = originalChain;
        }

        //2. 执行过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 创建访问实例
     * @author zifangsky
     * @date 2019/5/16 13:46
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return cn.zifangsky.easylimit.access.Access
     */
    protected Access createAccess(HttpServletRequest request, HttpServletResponse response){
        return new Access.Builder(this.securityManager, request, response).build();
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public FilterChainResolver getFilterChainResolver() {
        return filterChainResolver;
    }

    public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
        this.filterChainResolver = filterChainResolver;
    }
}
