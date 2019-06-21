package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 处理权限注解的{@link MethodInterceptor}
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public abstract class AbstractAnnotationMethodInterceptor implements MethodInterceptor {
    /**
     * 校验角色、权限
     * @author zifangsky
     * @date 2019/6/19 18:33
     * @since 1.0.0
     * @param invocation MethodInvocation
     */
    protected abstract void assertPermission(MethodInvocation invocation) throws AuthenticationException;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        this.assertPermission(invocation);
        return invocation.proceed();
    }
}
