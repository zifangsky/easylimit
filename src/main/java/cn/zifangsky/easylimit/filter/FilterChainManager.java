package cn.zifangsky.easylimit.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 代理的{@link FilterChain}管理器
 *
 * @author zifangsky
 * @date 2019/5/9
 * @since 1.0.0
 */
public interface FilterChainManager {

    /**
     * 根据filter的名称获取filter
     * @author zifangsky
     * @date 2019/5/9 16:03
     * @since 1.0.0
     * @param filterName filter的名称
     * @return javax.servlet.Filter
     */
    Filter getFilter(String filterName);

    /**
     * 获取所有可用的filter
     * @author zifangsky
     * @date 2019/5/9 16:03
     * @since 1.0.0
     * @return java.util.Map<java.lang.String,javax.servlet.Filter>
     */
    Map<String, Filter> getAvailableFilters();

    /**
     * 获取某个特定拦截路径需要执行的filter
     * @author zifangsky
     * @date 2019/5/9 16:06
     * @since 1.0.0
     * @param patternPath 某个特定拦截路径
     * @return java.util.List<javax.servlet.Filter>
     */
    List<Filter> getPatternPathFilters(String patternPath);

    /**
     * 获取所有的特定的拦截路径
     * @author zifangsky
     * @date 2019/5/9 16:37
     * @since 1.0.0
     * @return java.util.Set<java.lang.String>
     */
    Set<String> getPatternPaths();

    /**
     * 是否存在FilterChain
     * @author zifangsky
     * @date 2019/5/9 16:39
     * @since 1.0.0
     * @return boolean
     */
    boolean hasFilterChain();

    /**
     * 获取代理之后的FilterChain
     * @author zifangsky
     * @date 2019/5/9 16:42
     * @since 1.0.0
     * @param original 原过滤链
     * @param patternPath 某个特定拦截路径
     * @return javax.servlet.FilterChain
     */
    FilterChain getProxiedFilterChain(FilterChain original, String patternPath);

    /**
     * 添加filter
     * @author zifangsky
     * @date 2019/5/9 16:50
     * @since 1.0.0
     * @param filterName filter的名称
     * @param filter filter
     */
    void addFilter(String filterName, Filter filter);

    /**
     * 添加filter
     * @author zifangsky
     * @date 2019/5/9 16:50
     * @since 1.0.0
     * @param filterName filter的名称
     * @param filter filter
     * @param init 是否使用FilterConfig初始化
     */
    void addFilter(String filterName, Filter filter, boolean init);

    /**
     * 添加filter
     * @author zifangsky
     * @date 2019/5/9 16:50
     * @since 1.0.0
     * @param filterName filter的名称
     * @param filter filter
     * @param init 是否使用FilterConfig初始化
     * @param overwrite 是否覆盖前面的配置
     */
    void addFilter(String filterName, Filter filter, boolean init, boolean overwrite);

    /**
     * 创建某个特定拦截路径的专有FilterChain
     * @author zifangsky
     * @date 2019/5/9 17:04
     * @since 1.0.0
     * @param patternPath 某个特定拦截路径，如：<b>/aaa/bbb/**</b>
     * @param filterExpressionArr filter表达式数组，如：<b>login, roles[reviewer, subscriber], perms[list, edit]</b>
     */
    void createFilterChain(String patternPath, String...filterExpressionArr);

    /**
     * 创建某个特定拦截路径的专有FilterChain
     * @author zifangsky
     * @date 2019/5/9 17:04
     * @since 1.0.0
     * @param patternPath 某个特定拦截路径
     * @param filterName filter的名称
     */
    void addToFilterChain(String patternPath, String filterName);

    /**
     * 创建某个特定拦截路径的专有FilterChain
     * @author zifangsky
     * @date 2019/5/9 17:04
     * @since 1.0.0
     * @param patternPath 某个特定拦截路径
     * @param filterName filter的名称
     * @param controlVal 多个具体的角色或权限要求，比如：<b>reviewer, subscriber</b>
     */
    void addToFilterChain(String patternPath, String filterName, String[] controlVal);
}
