package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.permission.annotation.Logical;
import cn.zifangsky.easylimit.permission.annotation.RequiresPermissions;

import java.lang.annotation.Annotation;

/**
 * 权限相关AOP处理器
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class PermissionsAnnotationResolver extends AbstractAnnotationResolver {

    public PermissionsAnnotationResolver() {
        super(RequiresPermissions.class);
    }

    @Override
    public void assertPermission(Annotation annotation) throws AuthenticationException {
        if(!(annotation instanceof RequiresPermissions)){
            return;
        }

        RequiresPermissions requiresPermissions = (RequiresPermissions)annotation;
        Access access = this.getAccess();

        //1. 获取所有权限码
        String[] perms = requiresPermissions.value();

        if(perms.length == 1){
            access.checkPermission(perms[0]);
            return;
        }

        if(Logical.AND.equals(requiresPermissions.logical())){
            access.checkAllPermissions(perms);
            return;
        }

        if(Logical.OR.equals(requiresPermissions.logical())){
            access.checkAnyPermissions(perms);
        }
    }
}
