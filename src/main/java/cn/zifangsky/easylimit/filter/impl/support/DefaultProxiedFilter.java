package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.filter.FilterChainResolver;
import cn.zifangsky.easylimit.session.Session;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 默认的{@link AbstractProxiedFilter}
 *
 * @author zifangsky
 * @date 2019/5/15
 * @since 1.0.0
 */
public class DefaultProxiedFilter extends AbstractProxiedFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProxiedFilter.class);

    public DefaultProxiedFilter(SecurityManager securityManager, FilterChainResolver filterChainResolver) {
        super(securityManager, filterChainResolver);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain) throws ServletException, IOException {
        try {
            //1. 创建访问实例
            Access access = this.createAccess(request, response);

            //2. 执行过滤链
            access.execute(() -> {
                //2.1 更新session的访问时间
                updateSessionLatestAccessTime(request);

                //2.2 执行过滤链
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

    /**
     * 刷新session的最新访问时间
     * @author zifangsky
     * @date 2019/5/16 14:19
     * @since 1.0.0
     * @param request HttpServletRequest
     */
    protected void updateSessionLatestAccessTime(HttpServletRequest request){
        Access access = SecurityUtils.getAccess();
        Session session = access.getSession(false);

        if(session != null){
            try {
                session.refresh();
            }catch (Exception e){
                LOGGER.error("The session.refresh() method failed to execute.", e);
            }
        }
    }
}
