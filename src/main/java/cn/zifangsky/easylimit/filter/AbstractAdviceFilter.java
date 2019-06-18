package cn.zifangsky.easylimit.filter;

import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * 登录、角色、权限等业务功能的抽象{@link Filter}
 *
 * @author zifangsky
 * @date 2019/4/30
 * @since 1.0.0
 */
public abstract class AbstractAdviceFilter extends AbstractOncePerRequestFilter{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdviceFilter.class);

    /**
     * 项目模式
     */
    private ProjectModeEnums projectMode = ProjectModeEnums.DEFAULT;

    /**
     * 默认不做处理，继续执行后面的过滤链
     */
    protected void executeFilterChain(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

    /**
     * 校验某些条件，通过返回值判断是否继续执行后面的过滤链
     */
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }

    /**
     * 请求返回之前执行某些操作
     */
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {

    }

    /**
     * 请求结束之后执行某些操作
     */
    protected void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Exception exception = null;
        try {
            //1. 校验是否继续执行后面的过滤链
            boolean continueChain = this.preHandle(request, response);
            LOGGER.debug(MessageFormat.format("The preHandle method for [{0} filter] has been executed and the result is [{1}].", this.getFilterName(), continueChain));

            //2. 如果结果为true，则继续执行后面的过滤链
            if(continueChain){
                this.executeFilterChain(request, response, filterChain);
                LOGGER.debug(MessageFormat.format("The executeFilterChain method for [{0} filter] has been executed.", this.getFilterName()));
            }

            //3. 请求返回之前执行某些操作
            this.postHandle(request, response);
            LOGGER.debug(MessageFormat.format("The postHandle method for [{0} filter] has been executed.", this.getFilterName()));
        }catch (Exception e){
            exception = e;
        }finally {
            //4. 执行清理工作
            this.cleanup(request, response, exception);
        }
    }

    /**
     * 执行清理工作
     */
    protected void cleanup(ServletRequest request, ServletResponse response, Exception exception)
            throws ServletException, IOException {
        try {
            this.afterCompletion(request, response, exception);
            LOGGER.debug(MessageFormat.format("The afterCompletion method for [{0} filter] has been executed.", this.getFilterName()));
        } catch (Exception e) {
            if(e instanceof ServletException){
                throw (ServletException)e;
            }else if(e instanceof IOException){
                throw (IOException)e;
            }else{
                LOGGER.error("Filter returns an unexpected exception during execution.", e);
                throw new ServletException(e);
            }
        }
    }

    /**
     * 重定向
     */
    protected void doRedirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl) throws IOException {
        WebUtils.executeRedirect(request, response, redirectUrl);
    }

    public ProjectModeEnums getProjectMode() {
        return projectMode;
    }

    public void setProjectMode(ProjectModeEnums projectMode) {
        this.projectMode = projectMode;
    }
}
