package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.impl.TokenAccessContext;
import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;
import cn.zifangsky.easylimit.exception.authc.NoPermissionException;
import cn.zifangsky.easylimit.exception.authc.NoRoleException;
import cn.zifangsky.easylimit.exception.authc.NotLoginException;
import cn.zifangsky.easylimit.exception.token.ExpiredTokenException;
import cn.zifangsky.easylimit.exception.token.InvalidTokenException;
import cn.zifangsky.easylimit.filter.FilterChainResolver;
import cn.zifangsky.easylimit.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基于Token模式的{@link AbstractProxiedFilter}
 *
 * @author zifangsky
 * @date 2019/5/15
 * @since 1.0.0
 */
public class TokenProxiedFilter extends AbstractProxiedFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProxiedFilter.class);

    /**
     * Token模式的“TOKEN不可用”情况的错误返回
     */
    private TokenRespMsg invalidTokenRespMsg = new TokenRespMsg(DefaultTokenRespEnums.INVALID_TOKEN.getCode(), DefaultTokenRespEnums.INVALID_TOKEN.getMsg());

    /**
     * Token模式的“TOKEN过期”情况的错误返回
     */
    private TokenRespMsg expiredTokenRespMsg = new TokenRespMsg(DefaultTokenRespEnums.EXPIRED_TOKEN.getCode(), DefaultTokenRespEnums.EXPIRED_TOKEN.getMsg());

    /**
     * Token模式的“没有指定权限”情况的错误返回
     */
    private TokenRespMsg noPermissionsRespMsg = new TokenRespMsg(DefaultTokenRespEnums.NO_PERMISSIONS.getCode(), DefaultTokenRespEnums.NO_PERMISSIONS.getMsg());

    /**
     * Token模式的“未登录”情况的错误返回
     */
    private TokenRespMsg notLoginRespMsg = new TokenRespMsg(DefaultTokenRespEnums.UN_LOGIN.getCode(), DefaultTokenRespEnums.UN_LOGIN.getMsg());

    /**
     * Token模式的“系统异常”情况的错误返回
     */
    private TokenRespMsg systemErrorRespMsg = new TokenRespMsg(DefaultTokenRespEnums.SYSTEM_ERROR.getCode(), DefaultTokenRespEnums.SYSTEM_ERROR.getMsg());


    public TokenProxiedFilter(SecurityManager securityManager, FilterChainResolver filterChainResolver) {
        super(securityManager, filterChainResolver);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain) throws ServletException, IOException {
        try {
            //1. 创建访问实例
            Access access = this.createAccess(request, response);

            //2. 执行过滤链
            access.execute(() -> {
                executeFilterChain(request, response, originalChain);

                return null;
            });
        }catch (Exception e){
            TokenRespMsg tokenRespMsg = this.systemErrorRespMsg;

            //返回token不可用的提示
            if(e instanceof InvalidTokenException){
                tokenRespMsg = this.invalidTokenRespMsg;
            }
            //返回token过期的提示
            else if(e instanceof ExpiredTokenException){
                tokenRespMsg = this.expiredTokenRespMsg;
            }
            else{
                Throwable cause = e.getCause().getCause();
                if(cause != null){
                    //返回没有权限访问的提示
                    if ((cause instanceof NoPermissionException) || (cause instanceof NoRoleException)){
                        tokenRespMsg = this.noPermissionsRespMsg;
                    }
                    //返回未登录的提示
                    else if((cause instanceof NotLoginException)){
                        tokenRespMsg = this.notLoginRespMsg;
                    }
                }
            }

            try {
                this.generateTokenResponse(WebUtils.toHttp(response), tokenRespMsg);
            } catch (Exception ex) {
                //ignore
            }
        }
    }

    @Override
    protected Access createAccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new Access.Builder(this.getSecurityManager(), new TokenAccessContext(), request, response).build();
    }
}
