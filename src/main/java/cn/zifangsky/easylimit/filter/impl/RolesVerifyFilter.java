package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.filter.AbstractVerifyFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色相关校验的{@link Filter}
 *
 * @author zifangsky
 * @date 2019/5/8
 * @since 1.0.0
 */
public class RolesVerifyFilter extends AbstractVerifyFilter {

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, String[] rolesArray) throws Exception {
        if(rolesArray == null || rolesArray.length == 0){
            return true;
        }else{
            Access access = this.getAccess(request, response);
            return access.hasAllRoles(rolesArray);
        }
    }
}
