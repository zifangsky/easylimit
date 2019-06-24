package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.filter.AbstractAdviceFilter;
import cn.zifangsky.easylimit.filter.impl.support.TokenRespMsg;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import cn.zifangsky.easylimit.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * 默认的注销{@link Filter}
 *
 * @author zifangsky
 * @date 2019/4/30
 * @since 1.0.0
 */
public class DefaultLogoutFilter extends AbstractAdviceFilter{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLogoutFilter.class);

    /**
     * Token模式的未登录情况的错误返回
     */
    private TokenRespMsg logoutRespMsg = new TokenRespMsg(DefaultTokenRespEnums.LOGOUT);

    /**
     * 注销之后的重定向URL
     */
    private String logoutRedirectUrl = Constants.DEFAULT_LOGOUT_REDIRECT_URL;

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //1. 获取当前访问实例
        Access access = this.getAccess(request, response);

        //2.注销登录，清空相关缓存，停用session等
        try {
            access.logout();
        }catch (Exception e){
            LOGGER.error("Logout failed!", e);
        }

        //3. 重定向或返回提示信息
        if(ProjectModeEnums.DEFAULT.equals(this.getProjectMode())){
            this.doRedirect(WebUtils.toHttp(request), WebUtils.toHttp(response), this.logoutRedirectUrl);
        }else if(ProjectModeEnums.TOKEN.equals(this.getProjectMode())){
            this.generateTokenResponse(WebUtils.toHttp(response), this.logoutRespMsg);
        }

        return false;
    }

    /**
     * 获取访问实例
     */
    protected Access getAccess(ServletRequest request, ServletResponse response) {
        return SecurityUtils.getAccess();
    }

    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    public void setLogoutRedirectUrl(String logoutRedirectUrl) {
        this.logoutRedirectUrl = logoutRedirectUrl;
    }

    public TokenRespMsg getLogoutRespMsg() {
        return logoutRespMsg;
    }

    public void setLogoutRespMsg(TokenRespMsg logoutRespMsg) {
        this.logoutRespMsg = logoutRespMsg;
    }
}
