package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.permission.annotation.RequiresLogin;

import java.lang.annotation.Annotation;

/**
 * 登录认证相关AOP处理器
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class LoginAnnotationResolver extends AbstractAnnotationResolver {

    public LoginAnnotationResolver() {
        super(RequiresLogin.class);
    }

    @Override
    public void assertPermission(Annotation annotation) throws AuthenticationException {
        if(!(annotation instanceof RequiresLogin)){
            return;
        }

        Access access = this.getAccess();
        access.checkPrincipal();
    }
}
