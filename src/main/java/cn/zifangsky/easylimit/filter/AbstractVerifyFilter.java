package cn.zifangsky.easylimit.filter;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.filter.impl.support.TokenRespMsg;
import cn.zifangsky.easylimit.utils.WebUtils;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色权限相关校验的{@link Filter}
 *
 * @author zifangsky
 * @date 2019/5/7
 * @since 1.0.0
 */
public abstract class AbstractVerifyFilter extends AbstractAccessControlFilter{

    /**
     * Token模式的没有权限情况的错误返回
     */
    private TokenRespMsg unauthorizedRespMsg = new TokenRespMsg(DefaultTokenRespEnums.NO_PERMISSIONS);

    /**
     * 认证失败跳转的URL
     */
    private String unauthorizedUrl = Constants.DEFAULT_UNAUTHORIZED_URL;

    @Override
    protected boolean afterAccessDenied(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        if(ProjectModeEnums.DEFAULT.equals(this.getProjectMode()) && !WebUtils.isAjaxRequest(request)){
            Access access = this.getAccess(request, response);

            //如果还没有登录，则跳转到登录页面
            if(access != null && !access.isAuthenticated()){
                this.saveSourceUrlAndRedirectToLoginPage(request, response, null);
            }else{
                this.doRedirect(request, response, this.unauthorizedUrl);
            }
        }else{
            this.generateTokenResponse(response, this.unauthorizedRespMsg);
        }

        return false;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public TokenRespMsg getUnauthorizedRespMsg() {
        return unauthorizedRespMsg;
    }

    public void setUnauthorizedRespMsg(TokenRespMsg unauthorizedRespMsg) {
        this.unauthorizedRespMsg = unauthorizedRespMsg;
    }
}
