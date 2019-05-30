package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.filter.AbstractAccessControlFilter;
import cn.zifangsky.easylimit.filter.impl.support.TokenRespMsg;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 默认的登录{@link Filter}
 *
 * @author zifangsky
 * @date 2019/5/6
 * @since 1.0.0
 */
public class DefaultLoginFilter extends AbstractAccessControlFilter{

    /**
     * Token模式的未登录情况的错误返回
     */
    private TokenRespMsg unLoginRespMsg = new TokenRespMsg(DefaultTokenRespEnums.UN_LOGIN.getCode(), DefaultTokenRespEnums.UN_LOGIN.getMsg());

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        Access access = this.getAccess(request, response);
        //返回是否已经登录成功的标识
        return access.isAuthenticated();
    }

    @Override
    protected boolean afterAccessDenied(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        if(ProjectModeEnums.DEFAULT.equals(this.getProjectMode())){
            //跳转到登录页面
            this.saveSourceUrlAndRedirectToLoginPage(request, response);
        }else if(ProjectModeEnums.TOKEN.equals(this.getProjectMode())){
            this.generateTokenResponse(response, this.unLoginRespMsg);
        }

        return false;
    }

    public TokenRespMsg getUnLoginRespMsg() {
        return unLoginRespMsg;
    }

    public void setUnLoginRespMsg(TokenRespMsg unLoginRespMsg) {
        this.unLoginRespMsg = unLoginRespMsg;
    }
}
