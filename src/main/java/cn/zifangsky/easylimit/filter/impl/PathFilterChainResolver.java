package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.filter.FilterChainManager;
import cn.zifangsky.easylimit.filter.FilterChainResolver;
import cn.zifangsky.easylimit.utils.AntPathMatcher;
import cn.zifangsky.easylimit.utils.PatternMatcher;
import cn.zifangsky.easylimit.utils.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基于路径匹配场景获取代理{@link FilterChain}
 *
 * @author zifangsky
 * @date 2019/5/10
 * @since 1.0.0
 */
public class PathFilterChainResolver implements FilterChainResolver{
    /**
     * 代理的{@link FilterChain}管理器
     */
    private FilterChainManager filterChainManager;

    /**
     * 使用正则表达式匹配路径
     */
    private PatternMatcher patternMatcher;

    public PathFilterChainResolver() {
        this.filterChainManager = new DefaultFilterChainManager();
        this.patternMatcher = new AntPathMatcher();
    }

    public PathFilterChainResolver(FilterConfig filterConfig) {
        this.filterChainManager = new DefaultFilterChainManager(filterConfig);
        this.patternMatcher = new AntPathMatcher();
    }

    public PathFilterChainResolver(FilterChainManager filterChainManager) {
        this(filterChainManager, new AntPathMatcher());
    }

    public PathFilterChainResolver(FilterChainManager filterChainManager, PatternMatcher patternMatcher) {
        this.filterChainManager = filterChainManager;
        this.patternMatcher = patternMatcher;
    }

    @Override
    public FilterChain getProxiedFilterChain(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain) {
        //1. 遍历所有需要额外执行filter的拦截路径
        for(String patternPath : this.filterChainManager.getPatternPaths()){
            //2. 判断是否和请求路径匹配
            if(this.matchPath(patternPath, request)){
                //3. 获取代理后的FilterChain
                return this.filterChainManager.getProxiedFilterChain(originalChain, patternPath);
            }
        }

        return null;
    }


    /**
     * 校验请求URI和预设的正则类型的拦截路径是否匹配
     */
    protected boolean matchPath(String patternPath, HttpServletRequest httpServletRequest){
        //获取不包含当前域名和项目名的URI
        String servletPath = WebUtils.getServletPath(httpServletRequest);
        return this.matchPath(patternPath, servletPath);
    }

    /**
     * 校验请求URI和预设的正则类型的拦截路径是否匹配
     */
    protected boolean matchPath(String patternPath, String servletPath){
        return patternMatcher.match(patternPath, servletPath);
    }

    public FilterChainManager getFilterChainManager() {
        return filterChainManager;
    }

    public void setFilterChainManager(FilterChainManager filterChainManager) {
        this.filterChainManager = filterChainManager;
    }

    public PatternMatcher getPatternMatcher() {
        return patternMatcher;
    }

    public void setPatternMatcher(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }
}
