package cn.zifangsky.easylimit.filter;

import cn.zifangsky.easylimit.utils.AntPathMatcher;
import cn.zifangsky.easylimit.utils.PatternMatcher;
import cn.zifangsky.easylimit.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 路径校验相关的抽象{@link Filter}
 *
 * @author zifangsky
 * @date 2019/4/30
 * @since 1.0.0
 */
public abstract class AbstractPathFilter extends AbstractAdviceFilter{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPathFilter.class);

    /**
     * 校验请求URI和预设的正则类型的拦截路径是否匹配
     */
    protected PatternMatcher patternMatcher = new AntPathMatcher();

    /**
     * 预设的正则类型的拦截路径
     * <p>示例：</p>
     * <p>KEY: /css/**</p>
     * <p>VALUE: admin,user</p>
     */
    protected Map<String, String[]> patternPathMap = new LinkedHashMap<>();

    /**
     * 给当前filter添加正则类型的拦截路径
     * @author zifangsky
     * @date 2019/4/30 15:37
     * @since 1.0.0
     * @param patternPath 拦截路径，比如：/css/**
     * @param controlVal 通过当前filter需要的角色、资源名称等，比如：admin,user
     * @return javax.servlet.Filter
     */
    public Filter addPatternPathConfig(String patternPath, String[] controlVal){
        if(!patternPathMap.containsKey(patternPath)){
            patternPathMap.put(patternPath, controlVal);
        }else{
            //如果可以覆盖，那么也设置
            if(this.overridePreviousPatternPath()){
                patternPathMap.put(patternPath, controlVal);
            }
        }

        return this;
    }

    /**
     * preHandle方法的真正校验逻辑
     * @author zifangsky
     * @date 2019/4/30 15:13
     * @since 1.0.0
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param controlVal 通过当前filter需要的角色、资源名称等
     * @return boolean
     */
    protected boolean doPreHandle(HttpServletRequest request,
                                  HttpServletResponse response, String[] controlVal) throws Exception{
        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        if(patternPathMap == null || patternPathMap.isEmpty()){
            LOGGER.debug("Because the pattern path is not preset, the filter will no longer intercept.");
            return true;
        }

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);

        //查找正则类型的拦截路径，然后执行
        for(String patternPath : patternPathMap.keySet()){
            if(this.matchPath(patternPath, httpServletRequest)){
                //获取通过当前filter需要的角色、资源名称等
                String[] controlVal = patternPathMap.get(patternPath);

                LOGGER.debug(MessageFormat.format("Start executing the doPreHandle method, patternPath [{0}], controlVal [{1}].", patternPath, controlVal));
                return this.doPreHandle(httpServletRequest, httpServletResponse, controlVal);
            }
        }

        return true;
    }

    /**
     * 校验请求URI和预设的正则类型的拦截路径是否匹配
     */
    protected boolean matchPath(String patternPath, HttpServletRequest httpServletRequest){
        //获取不包含当前域名和项目名的URI
        String servletPath = WebUtils.getServletPath(httpServletRequest);
        return this.matchPath(patternPath, servletPath);
    }

    /**
     * 校验请求URI和预设的正则类型的拦截路径是否匹配
     */
    protected boolean matchPath(String patternPath, String servletPath){
        return patternMatcher.match(patternPath, servletPath);
    }

    /**
     * 是否覆盖前面设置的 <b>pattern path</b>
     */
    protected boolean overridePreviousPatternPath(){
        return true;
    }
}
