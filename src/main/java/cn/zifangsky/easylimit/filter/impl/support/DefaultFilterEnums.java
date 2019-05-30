package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.exception.filter.FilterException;
import cn.zifangsky.easylimit.filter.AbstractFilter;
import cn.zifangsky.easylimit.filter.impl.AnonymousFilter;
import cn.zifangsky.easylimit.filter.impl.DefaultLoginFilter;
import cn.zifangsky.easylimit.filter.impl.DefaultLogoutFilter;
import cn.zifangsky.easylimit.filter.impl.PermissionsVerifyFilter;
import cn.zifangsky.easylimit.filter.impl.RolesVerifyFilter;
import cn.zifangsky.easylimit.utils.BeanUtils;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认的filter的枚举
 *
 * @author zifangsky
 * @date 2019/5/9
 * @since 1.0.0
 */
public enum DefaultFilterEnums {
    /**
     * 匿名访问的filter
     */
    ANONYMOUS("anon", AnonymousFilter.class),
    /**
     * 需要登录的filter
     */
    LOGIN("login", DefaultLoginFilter.class),
    /**
     * 注销的filter
     */
    LOGOUT("logout", DefaultLogoutFilter.class),
    /**
     * 角色校验的filter
     */
    ROLES("roles", RolesVerifyFilter.class),
    /**
     * 权限校验的filter
     */
    PERMS("perms", PermissionsVerifyFilter.class)
    ;

    /**
     * filter的名称
     */
    private String filterName;

    private Class<? extends Filter> filterClass;

    DefaultFilterEnums(String filterName, Class<? extends AbstractFilter> filterClass) {
        this.filterName = filterName;
        this.filterClass = filterClass;
    }

    /**
     * 实例化
     */
    public Filter newInstance(){
        return BeanUtils.newInstance(this.filterClass);
    }

    /**
     * 创建默认filter的实例
     */
    public static Map<String, Filter> createDefaultFilterMap(FilterConfig filterConfig){
        Map<String, Filter> result = new LinkedHashMap<>(values().length);

        for(DefaultFilterEnums defaultFilterEnum : values()){
            Filter filter = defaultFilterEnum.newInstance();

            if(filterConfig != null){
                try {
                    filter.init(filterConfig);
                } catch (ServletException e) {
                    throw new FilterException(MessageFormat.format("Unable to correctly init default filter instance [{0}]", filter.getClass().getName()));
                }
            }

            result.put(defaultFilterEnum.getFilterName(), filter);
        }

        return result;
    }


    public String getFilterName() {
        return filterName;
    }

    public Class<? extends Filter> getFilterClass() {
        return filterClass;
    }
}
