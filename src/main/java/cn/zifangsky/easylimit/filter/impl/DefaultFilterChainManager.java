package cn.zifangsky.easylimit.filter.impl;

import cn.zifangsky.easylimit.exception.filter.FilterException;
import cn.zifangsky.easylimit.filter.AbstractFilter;
import cn.zifangsky.easylimit.filter.AbstractPathFilter;
import cn.zifangsky.easylimit.filter.FilterChainManager;
import cn.zifangsky.easylimit.filter.impl.support.DefaultFilterEnums;
import cn.zifangsky.easylimit.filter.impl.support.ProxiedFilterChain;
import cn.zifangsky.easylimit.utils.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 默认的{@link FilterChainManager}
 *
 * @author zifangsky
 * @date 2019/5/9
 * @since 1.0.0
 */
public class DefaultFilterChainManager implements FilterChainManager {
    /**
     * FilterConfig
     */
    private FilterConfig filterConfig;

    /**
     * <p>KEY: filter名称</p>
     * <p>VALUE: 对应的某个Filter</p>
     */
    private Map<String, Filter> availableFilterMap;

    /**
     * <p>KEY: 某个特定拦截路径</p>
     * <p>VALUE: 某个特定拦截路径需要执行的所有filter</p>
     */
    private Map<String, List<Filter>> patternPathFilterMap;

    public DefaultFilterChainManager() {
        this.availableFilterMap = new LinkedHashMap<>();
        this.patternPathFilterMap = new LinkedHashMap<>();
        //添加几个默认的filter
        this.addDefaultFilters(false);
    }

    public DefaultFilterChainManager(FilterConfig filterConfig) {
        if (filterConfig != null){
            this.filterConfig = filterConfig;
        }

        this.availableFilterMap = new LinkedHashMap<>();
        this.patternPathFilterMap = new LinkedHashMap<>();
        //添加几个默认的filter
        this.addDefaultFilters(true);
    }

    @Override
    public Filter getFilter(String filterName) {
        return this.availableFilterMap.get(filterName);
    }

    @Override
    public Map<String, Filter> getAvailableFilters() {
        return this.availableFilterMap;
    }

    @Override
    public List<Filter> getPatternPathFilters(String patternPath) {
        return this.patternPathFilterMap.get(patternPath);
    }

    @Override
    public Set<String> getPatternPaths() {
        return this.patternPathFilterMap.keySet();
    }

    @Override
    public boolean hasFilterChain() {
        return this.patternPathFilterMap != null && this.patternPathFilterMap.size() > 0;
    }

    @Override
    public FilterChain getProxiedFilterChain(FilterChain original, String patternPath) {
        List<Filter> filterList = this.patternPathFilterMap.get(patternPath);

        if(filterList != null){
            return new ProxiedFilterChain(original, filterList);
        }else{
            return null;
        }
    }

    @Override
    public void addFilter(String filterName, Filter filter) {
        this.addFilter(filterName, filter, false, true);
    }

    @Override
    public void addFilter(String filterName, Filter filter, boolean init) {
        this.addFilter(filterName, filter, init, true);
    }

    @Override
    public void addFilter(String filterName, Filter filter, boolean init, boolean overwrite) {
        Filter exist = this.getFilter(filterName);

        if(exist == null || overwrite){
            if(filter instanceof AbstractFilter){
                ((AbstractFilter) filter).setFilterName(filterName);
            }

            if(init){
                this.initFilter(filter);
            }

            this.availableFilterMap.put(filterName, filter);
        }
    }

    @Override
    public void createFilterChain(String patternPath, String...filterExpressionArr) {
        if(patternPath == null){
            throw new IllegalArgumentException("Parameter patternPath cannot be empty.");
        }
        if (filterExpressionArr == null) {
            throw new IllegalArgumentException("Parameter filterExpression cannot be empty.");
        }

        //1. 取出所有的filter要求
        List<String> filterLimitList = Arrays.asList(filterExpressionArr);

        for(String filterLimit : filterLimitList){
            //2. 再按照中括号分割，取出具体的“filter名称 + filter控制值”
            String[] nameControlArray = StringUtils.splitBySquareBrackets(filterLimit);

            String controlVal = nameControlArray[1];
            if(controlVal == null){
                //3. 给某个拦截路径添加新的需要执行的filter
                this.addToFilterChain(patternPath, nameControlArray[0], null);
            }else{
                //存在filter控制值的情况
                List<String> controlValList = StringUtils.splitByComma(controlVal);
                String[] controlValAray = controlValList.toArray(new String[controlValList.size()]);

                this.addToFilterChain(patternPath, nameControlArray[0], controlValAray);
            }
        }
    }

    @Override
    public void addToFilterChain(String patternPath, String filterName) {
        this.addToFilterChain(patternPath, filterName, null);
    }

    @Override
    public void addToFilterChain(String patternPath, String filterName, String[] controlVal) {
        //1. 获取filter
        Filter filter = availableFilterMap.get(filterName);
        if(filter == null){
            throw new IllegalArgumentException("Parameter filter cannot be empty.");
        }

        //2. 给filter添加控制值
        this.addPatternPathConfig(patternPath, filter, controlVal);

        List<Filter> filterList = patternPathFilterMap.get(patternPath);
        if(filterList == null){
            filterList = new LinkedList<>();
        }

        //3. 给某个拦截路径添加一个新的需要执行的filter
        filterList.add(filter);
        patternPathFilterMap.put(patternPath, filterList);
    }

    /**
     * 给filter添加控制值
     * @author zifangsky
     * @date 2019/5/10 14:52
     * @since 1.0.0
     * @param patternPath 某个特定拦截路径
     * @param filter 具体的filter，比如：roles
     * @param controlVal 多个具体的角色或权限要求，比如：<b>reviewer, subscriber</b>
     */
    protected void addPatternPathConfig(String patternPath, Filter filter, String[] controlVal){
        if(filter instanceof AbstractPathFilter){
            ((AbstractPathFilter) filter).addPatternPathConfig(patternPath, controlVal);
        }
    }

    /**
     * 初始化filter
     */
    protected void initFilter(Filter filter) {
        if(this.filterConfig != null && filter != null){
            try {
                filter.init(filterConfig);
            } catch (ServletException e) {
                throw new FilterException(e);
            }
        }
    }

    /**
     * 添加几个默认的filter
     */
    protected void addDefaultFilters(boolean init){
        for(DefaultFilterEnums defaultFilter : DefaultFilterEnums.values()){
            this.addFilter(defaultFilter.getFilterName(), defaultFilter.newInstance(), init, false);
        }
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public Map<String, Filter> getAvailableFilterMap() {
        return availableFilterMap;
    }

    public void setAvailableFilterMap(Map<String, Filter> availableFilterMap) {
        this.availableFilterMap = availableFilterMap;
    }

    public Map<String, List<Filter>> getPatternPathFilterMap() {
        return patternPathFilterMap;
    }

    public void setPatternPathFilterMap(Map<String, List<Filter>> patternPathFilterMap) {
        this.patternPathFilterMap = patternPathFilterMap;
    }
}
