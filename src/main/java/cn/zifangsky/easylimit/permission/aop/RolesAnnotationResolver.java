package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.permission.annotation.Logical;
import cn.zifangsky.easylimit.permission.annotation.RequiresRoles;

import java.lang.annotation.Annotation;

/**
 * 角色相关AOP处理器
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class RolesAnnotationResolver extends AbstractAnnotationResolver {

    public RolesAnnotationResolver() {
        super(RequiresRoles.class);
    }

    @Override
    public void assertPermission(Annotation annotation) throws AuthenticationException {
        if(!(annotation instanceof RequiresRoles)){
            return;
        }

        RequiresRoles requiresRoles = (RequiresRoles)annotation;
        Access access = this.getAccess();

        //1. 获取所有角色码
        String[] roles = requiresRoles.value();

        if(roles.length == 1){
            access.checkRole(roles[0]);
            return;
        }

        if(Logical.AND.equals(requiresRoles.logical())){
            access.checkAllRoles(roles);
            return;
        }

        if(Logical.OR.equals(requiresRoles.logical())){
            access.checkAnyRoles(roles);
        }
    }
}
