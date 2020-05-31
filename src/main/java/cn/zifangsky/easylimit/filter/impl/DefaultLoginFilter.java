package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.DefaultWebSecurityManager;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.filter.AbstractAccessControlFilter;
import cn.zifangsky.easylimit.filter.impl.support.TokenRespMsg;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import cn.zifangsky.easylimit.utils.WebUtils;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

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
    private TokenRespMsg unLoginRespMsg = new TokenRespMsg(DefaultTokenRespEnums.UN_LOGIN);

    /**
     * 被踢出情况的AJAX错误返回
     */
    private TokenRespMsg kickOutRespMsg = new TokenRespMsg(DefaultTokenRespEnums.KICKOUT);

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        Access access = this.getAccess(request, response);
        SecurityManager securityManager = SecurityUtils.getSecurityManager();

        if(securityManager instanceof DefaultWebSecurityManager){
            DefaultWebSecurityManager defaultWebSecurityManager = (DefaultWebSecurityManager) securityManager;

            //如果已经设置“踢出当前用户的旧会话”
            if(defaultWebSecurityManager.isKickOutOldSessions()){
                //1. 获取标识
                Session session = access.getSession(false);
                Object kickedOutFlag = session.getAttribute(Constants.KICK_OUT_OLD_SESSIONS_NAME);
                if(kickedOutFlag != null){
                    try {
                        //2. 删除标识
                        session.removeAttribute(Constants.KICK_OUT_OLD_SESSIONS_NAME);
                        //3. 退出登录
                        access.logout();
                    }catch (Exception e){
                        //ignore
                    }finally {
                        request.setAttribute(Constants.KICK_OUT_OLD_SESSIONS_NAME, true);
                    }

                    return false;
                }
            }
        }

        //返回是否已经登录成功的标识
        return access.isAuthenticated();
    }

    @Override
    protected boolean afterAccessDenied(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        Object kickedOutFlag = request.getAttribute(Constants.KICK_OUT_OLD_SESSIONS_NAME);

        //需要跳转页面的情况
        if(ProjectModeEnums.DEFAULT.equals(this.getProjectMode()) && !WebUtils.isAjaxRequest(request)){
            Map<String, String> params = new HashMap<>(4);

            if(kickedOutFlag != null){
                params.put(Constants.KICK_OUT_OLD_SESSIONS_PARAM_NAME, "1");
            }

            //跳转到登录页面
            this.saveSourceUrlAndRedirectToLoginPage(request, response, params);
        }
        //其他情况返回AJAX提示
        else{
            TokenRespMsg respMsg = null;

            if(kickedOutFlag != null){
                respMsg = this.kickOutRespMsg;
            }else{
                respMsg = this.unLoginRespMsg;
            }

            this.generateTokenResponse(response, respMsg);
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
