package cn.zifangsky.easylimit.filter;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import cn.zifangsky.easylimit.utils.WebUtils;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 访问控制相关的抽象{@link Filter}
 *
 * @author zifangsky
 * @date 2019/4/30
 * @since 1.0.0
 */
public abstract class AbstractAccessControlFilter extends AbstractPathFilter{
    /**
     * 登录URL
     */
    private String loginUrl = Constants.DEFAULT_LOGIN_URL;

    /**
     * 登录校验URL
     */
    private String loginCheckUrl = Constants.DEFAULT_LOGIN_CHECK_URL;

    /**
     * 是否允许访问
     * @author zifangsky
     * @date 2019/5/6 16:52
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param controlVal 通过当前filter需要的角色、资源名称等
     * @return boolean
     * @throws Exception Exception
     */
    protected abstract boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception;

    /**
     * 当没有访问许可时，可以做的其他事，比如：
     * <ul>
     *     <li>执行登录流程</li>
     *     <li>返回指定提示信息</li>
     * </ul>
     *
     * @author zifangsky
     * @date 2019/5/6 16:54
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param controlVal 通过当前filter需要的角色、资源名称等
     * @return boolean
     * @throws Exception Exception
     */
    protected abstract boolean afterAccessDenied(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception;

    @Override
    protected boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        return this.isAccessAllowed(request, response, controlVal)
                || this.afterAccessDenied(request,response, controlVal);
    }

    /**
     * <p>1. 保存来源URL</p>
     * <p>1. 重定向到登录页面</p>
     */
    protected void saveSourceUrlAndRedirectToLoginPage(HttpServletRequest request, HttpServletResponse response, String param) throws IOException {
        this.saveSourceUrl(request);
        this.redirectToLoginPage(request, response, param);
    }

    /**
     * 保存来源URL，目的是方便登录成功之后跳转回去
     */
    protected void saveSourceUrl(HttpServletRequest request){
        WebUtils.saveSourceUrl(request);
    }

    /**
     * 重定向到登录页面
     */
    protected void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response, String param) throws IOException {
        String resultUrl = (param != null ? (this.loginUrl + param) : this.loginUrl);
        this.doRedirect(request, response, resultUrl);
    }

    protected Access getAccess(HttpServletRequest request, HttpServletResponse response){
        return SecurityUtils.getAccess();
    }

    protected SecurityManager getSecurityManager(HttpServletRequest request, HttpServletResponse response){
        return SecurityUtils.getSecurityManager();
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLoginCheckUrl() {
        return loginCheckUrl;
    }

    public void setLoginCheckUrl(String loginCheckUrl) {
        this.loginCheckUrl = loginCheckUrl;
    }
}
