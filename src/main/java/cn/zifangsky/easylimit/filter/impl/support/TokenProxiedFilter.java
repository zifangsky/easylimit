package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.access.impl.TokenAccessContext;
import cn.zifangsky.easylimit.enums.DefaultTokenRespEnums;
import cn.zifangsky.easylimit.exception.token.ExpiredTokenException;
import cn.zifangsky.easylimit.exception.token.InvalidTokenException;
import cn.zifangsky.easylimit.filter.FilterChainResolver;
import cn.zifangsky.easylimit.utils.WebUtils;

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
    /**
     * Token模式的“TOKEN不可用”情况的错误返回
     */
    private TokenRespMsg invalidTokenRespMsg = new TokenRespMsg(DefaultTokenRespEnums.INVALID_TOKEN.getCode(), DefaultTokenRespEnums.INVALID_TOKEN.getMsg());

    /**
     * Token模式的“TOKEN过期”情况的错误返回
     */
    private TokenRespMsg expiredTokenRespMsg = new TokenRespMsg(DefaultTokenRespEnums.EXPIRED_TOKEN.getCode(), DefaultTokenRespEnums.EXPIRED_TOKEN.getMsg());


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
            if (e instanceof IOException) {
                throw (IOException) e;
            }else if(e instanceof InvalidTokenException){

                //返回token不可用的提示
                try {
                    this.generateTokenResponse(WebUtils.toHttp(response), this.invalidTokenRespMsg);
                } catch (Exception ex) {
                    throw new ServletException(ex);
                }
            }else if(e instanceof ExpiredTokenException){
                //返回token过期的提示
                try {
                    this.generateTokenResponse(WebUtils.toHttp(response), this.expiredTokenRespMsg);
                } catch (Exception ex) {
                    throw new ServletException(ex);
                }
            }else{
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected Access createAccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new Access.Builder(this.getSecurityManager(), new TokenAccessContext(), request, response).build();
    }
}
