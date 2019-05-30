package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.filter.FilterChainResolver;
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
                //TODO 其他操作
                executeFilterChain(request, response, originalChain);

                return null;
            });
        }catch (Exception e){
            if (e instanceof IOException) {
                throw (IOException) e;
            }else{
                throw new ServletException(e);
            }
        }
    }
}
