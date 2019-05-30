package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.filter.AbstractVerifyFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 匿名访问的{@link Filter}
 *
 * @author zifangsky
 * @date 2019/5/8
 * @since 1.0.0
 */
public class AnonymousFilter extends AbstractVerifyFilter {

    @Override
    protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, String[] controlVal) throws Exception {
        //始终返回true
        return true;
    }
}
