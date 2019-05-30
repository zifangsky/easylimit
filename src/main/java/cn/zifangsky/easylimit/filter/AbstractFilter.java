package cn.zifangsky.easylimit.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * {@link Filter}的基本实现
 *
 * @author zifangsky
 * @date 2019/4/29
 * @since 1.0.0
 */
public abstract class AbstractFilter implements Filter{
    /**
     * ServletContext
     */
    private ServletContext servletContext = null;

    /**
     * ServletContext
     */
    private FilterConfig filterConfig;

    /**
     * {@link Filter}的名称
     */
    private String filterName;

    /**
     * 允许子类在初始化{@link Filter}时做一些其他操作
     * @author zifangsky
     * @date 2019/4/29 11:15
     * @since 1.0.0
     */
    protected void onInitFilter() throws ServletException{

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.setFilterConfig(filterConfig);
        this.onInitFilter();
    }

    @Override
    public void destroy() {

    }

    /**
     * 获取{@link Filter}的初始参数
     * @author zifangsky
     * @date 2019/4/29 11:11
     * @since 1.0.0
     * @param paramName 参数名
     * @return java.lang.String
     */
    public String getInitParameter(String paramName){
        FilterConfig config = this.filterConfig;
        if(config != null){
            return config.getInitParameter(paramName);
        }

        return null;
    }

    /**
     * 向{@link ServletContext}设置键值对
     * @author zifangsky
     * @date 2019/4/29 10:58
     * @since 1.0.0
     * @param key KEY
     * @param value VALUE
     */
    public void setContextAttribute(String key, Object value) {
        if (value == null) {
            this.removeContextAttribute(key);
        } else {
            this.acquireServletContext().setAttribute(key, value);
        }
    }

    /**
     * 从{@link ServletContext}获取键值对
     * @author zifangsky
     * @date 2019/4/29 10:59
     * @since 1.0.0
     * @param key KEY
     * @return java.lang.Object
     */
    public Object getContextAttribute(String key) {
        return this.acquireServletContext().getAttribute(key);
    }

    /**
     * 从{@link ServletContext}移除键值对
     * @author zifangsky
     * @date 2019/4/29 11:00
     * @since 1.0.0
     * @param key KEY
     */
    public void removeContextAttribute(String key) {
        this.acquireServletContext().removeAttribute(key);
    }

    /**
     * 获取{@link ServletContext}，如果不存在则抛出异常
     * @author zifangsky
     * @date 2019/4/29 10:53
     * @since 1.0.0
     * @return javax.servlet.ServletContext
     */
    protected ServletContext acquireServletContext(){
        ServletContext context = this.servletContext;

        if(context == null){
            throw new IllegalArgumentException("Parameter servletContext cannot be empty.");
        }

        return context;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        if(filterConfig != null){
            this.filterConfig = filterConfig;
            this.setServletContext(filterConfig.getServletContext());
        }
    }

    public String getFilterName() {
        if(this.filterName == null){
            FilterConfig config = this.filterConfig;
            if(config != null){
                this.filterName = config.getFilterName();
            }
        }

        return this.filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
