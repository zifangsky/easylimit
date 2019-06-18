package cn.zifangsky.easylimit.filter.impl.support;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.common.Constants;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.exception.EasyLimitException;
import cn.zifangsky.easylimit.filter.AbstractAccessControlFilter;
import cn.zifangsky.easylimit.filter.AbstractAdviceFilter;
import cn.zifangsky.easylimit.filter.AbstractFilter;
import cn.zifangsky.easylimit.filter.AbstractVerifyFilter;
import cn.zifangsky.easylimit.filter.FilterChainManager;
import cn.zifangsky.easylimit.filter.FilterChainResolver;
import cn.zifangsky.easylimit.filter.impl.DefaultFilterChainManager;
import cn.zifangsky.easylimit.filter.impl.DefaultLogoutFilter;
import cn.zifangsky.easylimit.filter.impl.PathFilterChainResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于在Spring中注入所有框架自定义{@link Filter}的工厂类
 *
 * @author zifangsky
 * @date 2019/5/27
 * @since 1.0.0
 */
public class FilterRegistrationFactoryBean implements FactoryBean<AbstractProxiedFilter> {

    /**
     * 项目模式
     */
    private ProjectModeEnums projectMode = ProjectModeEnums.DEFAULT;

    /**
     * SecurityManager
     */
    private SecurityManager securityManager;

    /**
     * <p>用户额外添加的{@link Filter}</p>
     * <p>KEY: filter名称</p>
     * <p>VALUE: 对应的某个Filter</p>
     */
    private Map<String, Filter> customizedFilters;

    /**
     * <p>框架需要拦截校验的路径以及执行的filter的对应关系</p>
     * <p>KEY: 某个特定拦截路径，比如：/aaa/bbb/**</p>
     * <p>VALUE: 某个特定拦截路径需要执行的所有filter，比如：login, roles[reviewer, subscriber], perms[list, edit]</p>
     */
    private Map<String, String[]> patternPathFilterMap;

    /**
     * 登录URL
     */
    private String loginUrl;

    /**
     * 登录校验URL
     */
    private String loginCheckUrl;

    /**
     * 认证失败跳转的URL
     */
    private String unauthorizedUrl;

    /**
     * 注销之后的重定向URL
     */
    private String logoutRedirectUrl;

    /**
     * 代理filter
     */
    private AbstractProxiedFilter abstractProxiedFilter;

    public FilterRegistrationFactoryBean() {
        this.customizedFilters = new LinkedHashMap<>();
        this.patternPathFilterMap = new LinkedHashMap<>();
    }

    public FilterRegistrationFactoryBean(SecurityManager securityManager, LinkedHashMap<String, String[]> patternPathFilterMap) {
        this(ProjectModeEnums.DEFAULT, securityManager, patternPathFilterMap);
    }

    public FilterRegistrationFactoryBean(ProjectModeEnums projectMode, SecurityManager securityManager, LinkedHashMap<String, String[]> patternPathFilterMap) {
        if(projectMode == null){
            throw new IllegalArgumentException("Parameter projectMode cannot be empty.");
        }
        if(securityManager == null){
            throw new IllegalArgumentException("Parameter securityManager cannot be empty.");
        }
        if(patternPathFilterMap == null){
            throw new IllegalArgumentException("Parameter patternPathFilterMap cannot be empty.");
        }

        this.projectMode = projectMode;
        this.securityManager = securityManager;
        this.patternPathFilterMap = patternPathFilterMap;
    }



    @Override
    public AbstractProxiedFilter getObject() throws Exception {
        if(this.abstractProxiedFilter == null){
            this.abstractProxiedFilter = this.createProxiedFilter();
        }

        return this.abstractProxiedFilter;
    }

    @Override
    public Class<? extends AbstractProxiedFilter> getObjectType() {
        return AbstractProxiedFilter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 创建框架执行的入口
     * @author zifangsky
     * @date 2019/5/27 17:26
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.filter.impl.support.AbstractProxiedFilter
     */
    protected AbstractProxiedFilter createProxiedFilter() throws Exception{
        //1. 判断SecurityManager是否为空
        if(this.securityManager == null){
            throw new EasyLimitException("SecurityManager parameter cannot be empty.");
        }

        //2. 创建FilterChainManager
        FilterChainManager filterChainManager = this.createFilterChainManager();

        //3. 创建FilterChainResolver
        FilterChainResolver filterChainResolver = new PathFilterChainResolver(filterChainManager);

        //4. 返回指定的AbstractProxiedFilter
        if(ProjectModeEnums.TOKEN.equals(this.getProjectMode())){
            return new TokenProxiedFilter(this.securityManager, filterChainResolver);
        }else{
            return new DefaultProxiedFilter(this.securityManager, filterChainResolver);
        }
    }

    /**
     * 用于创建FilterChainManager
     */
    protected FilterChainManager createFilterChainManager() {
        //1. 创建实例
        FilterChainManager filterChainManager = new DefaultFilterChainManager();

        //2. 为默认的filter设置全局参数
        Map<String, Filter> defaultFilters = filterChainManager.getAvailableFilters();
        for(Filter filter : defaultFilters.values()){
            this.applyGlobalParametersIfNecessary(filter);
        }

        //3. 处理用户额外添加的filter
        if(this.customizedFilters != null && this.customizedFilters.size() > 0){
            this.customizedFilters.forEach((name, filter) -> {
                //3.1 设置filter名称
                if(filter instanceof AbstractFilter){
                    ((AbstractFilter) filter).setFilterName(name);
                }

                //3.2 为这些额外的filter设置全局参数
                this.applyGlobalParametersIfNecessary(filter);

                //3.3 添加到FilterChainManager中
                filterChainManager.addFilter(name, filter, false);
            });
        }

        //4. 处理过滤链
        if(this.patternPathFilterMap != null && this.patternPathFilterMap.size() > 0){
            Map<String, String[]> filterChainMap = new LinkedHashMap<>();

            //4.1 将默认的几个URL设置为可以匿名访问
            this.applyDefaultUrlsToAnonymousFilter(filterChainMap);

            //4.2 将其他过滤链添加到最终结果中
            filterChainMap.putAll(this.patternPathFilterMap);

            //4.3 创建某个特定拦截路径的专有FilterChain
            filterChainMap.forEach(filterChainManager::createFilterChain);
        }

        //5. 返回实例
        return filterChainManager;
    }

    /**
     * 设置全局参数
     */
    protected void applyGlobalParametersIfNecessary(Filter filter) {
        //1. 设置某个别filter需要的URL
        this.applyLoginUrlIfNecessary(filter);
        this.applyLoginCheckUrlIfNecessary(filter);
        this.applyUnauthorizedUrlIfNecessary(filter);
        this.applyLogoutRedirectUrlIfNecessary(filter);

        //2. 如果是Token模式，则还需要设置某个别filter需要的返回提示信息
        this.applyTokenResponseIfNecessary(filter);
    }

    /**
     * 将 <b>loginUrl、loginCheckUrl、unauthorizedUrl、logoutRedirectUrl</b> 这几个URL设置为可以匿名访问
     */
    private void applyDefaultUrlsToAnonymousFilter(Map<String, String[]> filterChainMap){
        if(StringUtils.isNoneBlank(this.loginUrl)){
            filterChainMap.put(this.loginUrl, new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        }
        if(StringUtils.isNoneBlank(this.loginCheckUrl)){
            filterChainMap.put(this.loginCheckUrl, new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        }
        if(StringUtils.isNoneBlank(this.unauthorizedUrl)){
            filterChainMap.put(this.unauthorizedUrl, new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        }
        if(StringUtils.isNoneBlank(this.logoutRedirectUrl)){
            filterChainMap.put(this.logoutRedirectUrl, new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        }
    }

    /**
     * 给{@link AbstractAdviceFilter}设置项目模式
     */
    private void applyTokenResponseIfNecessary(Filter filter){
        if(ProjectModeEnums.TOKEN.equals(this.projectMode) && (filter instanceof AbstractAdviceFilter)){
            AbstractAdviceFilter abstractAdviceFilter = (AbstractAdviceFilter) filter;
            abstractAdviceFilter.setProjectMode(this.projectMode);
        }
    }

    /**
     * 给{@link AbstractAccessControlFilter}设置登录URL
     */
    private void applyLoginUrlIfNecessary(Filter filter) {
        if(StringUtils.isNoneBlank(this.loginUrl) && (filter instanceof AbstractAccessControlFilter)){
            AbstractAccessControlFilter accessControlFilter = (AbstractAccessControlFilter) filter;

            if(!Constants.DEFAULT_LOGIN_URL.equals(this.loginUrl)){
                accessControlFilter.setLoginUrl(this.loginUrl);
            }
        }
    }

    /**
     * 给{@link AbstractAccessControlFilter}设置登录校验URL
     */
    private void applyLoginCheckUrlIfNecessary(Filter filter) {
        if(StringUtils.isNoneBlank(this.loginCheckUrl) && (filter instanceof AbstractAccessControlFilter)){
            AbstractAccessControlFilter accessControlFilter = (AbstractAccessControlFilter) filter;

            if(!Constants.DEFAULT_LOGIN_CHECK_URL.equals(this.loginCheckUrl)){
                accessControlFilter.setLoginCheckUrl(this.loginCheckUrl);
            }
        }
    }

    /**
     * 给{@link AbstractVerifyFilter}设置认证失败跳转的URL
     */
    private void applyUnauthorizedUrlIfNecessary(Filter filter) {
        if(StringUtils.isNoneBlank(this.unauthorizedUrl) && (filter instanceof AbstractVerifyFilter)){
            AbstractVerifyFilter verifyFilter = (AbstractVerifyFilter) filter;

            if(!Constants.DEFAULT_UNAUTHORIZED_URL.equals(this.unauthorizedUrl)){
                verifyFilter.setUnauthorizedUrl(this.unauthorizedUrl);
            }
        }
    }

    /**
     * 给{@link DefaultLogoutFilter}设置认证失败跳转的URL
     */
    private void applyLogoutRedirectUrlIfNecessary(Filter filter) {
        if(StringUtils.isNoneBlank(this.logoutRedirectUrl) && (filter instanceof DefaultLogoutFilter)){
            DefaultLogoutFilter logoutFilter = (DefaultLogoutFilter) filter;

            if(!Constants.DEFAULT_LOGOUT_REDIRECT_URL.equals(this.logoutRedirectUrl)){
                logoutFilter.setLogoutRedirectUrl(this.logoutRedirectUrl);
            }
        }
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLoginCheckUrl() {
        return loginCheckUrl;
    }

    public void setLoginCheckUrl(String loginCheckUrl) {
        this.loginCheckUrl = loginCheckUrl;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    public void setLogoutRedirectUrl(String logoutRedirectUrl) {
        this.logoutRedirectUrl = logoutRedirectUrl;
    }

    public ProjectModeEnums getProjectMode() {
        return projectMode;
    }

    public void setProjectMode(ProjectModeEnums projectMode) {
        this.projectMode = projectMode;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public Map<String, Filter> getCustomizedFilters() {
        return customizedFilters;
    }

    public void setCustomizedFilters(Map<String, Filter> customizedFilters) {
        this.customizedFilters = customizedFilters;
    }

    public Map<String, String[]> getPatternPathFilterMap() {
        return patternPathFilterMap;
    }

    public void setPatternPathFilterMap(Map<String, String[]> patternPathFilterMap) {
        this.patternPathFilterMap = patternPathFilterMap;
    }
}
