package cn.zifangsky.easylimit.filter.impl.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 代理的{@link FilterChain}
 *
 * @author zifangsky
 * @date 2019/5/9
 * @since 1.0.0
 */
public class ProxiedFilterChain implements FilterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxiedFilterChain.class);

    /**
     * 原过滤链
     */
    private FilterChain original;

    /**
     * 当前请求URL，在执行原过滤链之前需要执行的filter
     */
    private List<Filter> beforeFilters;

    /**
     * 用于计数
     */
    private int index = 0;

    public ProxiedFilterChain(FilterChain original, List<Filter> beforeFilters) {
        if(original == null){
            throw new IllegalArgumentException("Parameter original cannot be empty.");
        }

        this.original = original;
        this.beforeFilters = beforeFilters;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if(this.beforeFilters == null || this.beforeFilters.size() == this.index){
            LOGGER.debug("Now proceed to the original FilterChain");
            this.original.doFilter(request, response);
        }else{
            this.beforeFilters.get(this.index++).doFilter(request, response, this);
        }
    }
}
