# 开始使用 #

在项目的`pom.xml`中添加依赖：

```xml
<dependency>
    <groupId>cn.zifangsky</groupId>
    <artifactId>easylimit</artifactId>
    <version>${easylimit}</version>
</dependency>
```

## MVC项目开发模式 ##

#### （1）自定义登录方式，以及角色、权限相关信息的获取方式： ####

```java
package cn.zifangsky.easylimit.example.easylimit;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.authc.PrincipalInfo;
import cn.zifangsky.easylimit.authc.ValidatedInfo;
import cn.zifangsky.easylimit.authc.impl.SimplePrincipalInfo;
import cn.zifangsky.easylimit.authc.impl.UsernamePasswordValidatedInfo;
import cn.zifangsky.easylimit.example.mapper.SysFunctionMapper;
import cn.zifangsky.easylimit.example.mapper.SysRoleMapper;
import cn.zifangsky.easylimit.example.mapper.SysUserMapper;
import cn.zifangsky.easylimit.example.model.SysFunction;
import cn.zifangsky.easylimit.example.model.SysRole;
import cn.zifangsky.easylimit.example.model.SysUser;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.permission.PermissionInfo;
import cn.zifangsky.easylimit.permission.impl.SimplePermissionInfo;
import cn.zifangsky.easylimit.realm.impl.AbstractPermissionRealm;
import cn.zifangsky.easylimit.utils.SecurityUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义{@link cn.zifangsky.easylimit.realm.Realm}
 *
 * @author zifangsky
 * @date 2019/5/28
 * @since 1.0.0
 */
public class CustomRealm extends AbstractPermissionRealm {

    private SysUserMapper sysUserMapper;

    private SysRoleMapper sysRoleMapper;

    private SysFunctionMapper sysFunctionMapper;

    public CustomRealm(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysFunctionMapper sysFunctionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysFunctionMapper = sysFunctionMapper;
    }

    /**
     * 自定义“角色+权限”信息的获取方式
     */
    @Override
    protected PermissionInfo doGetPermissionInfo(PrincipalInfo principalInfo) {
        SimplePermissionInfo permissionInfo = null;

        //获取用户信息
        SysUser sysUser = (SysUser) principalInfo.getPrincipal();
        if(sysUser != null){

            //通过用户ID查询角色权限信息
            Set<SysRole> roleSet = sysRoleMapper.selectByUserId(sysUser.getId());
            if(roleSet != null && roleSet.size() > 0){
                //所有角色名
                Set<String> roleNames = new HashSet<>(roleSet.size());
                //所有权限的code集合
                Set<String> funcCodes = new HashSet<>();

                for(SysRole role : roleSet){
                    roleNames.add(role.getName());

                    Set<SysFunction> functionSet = sysFunctionMapper.selectByRoleId(role.getId());
                    if(functionSet != null && functionSet.size() > 0){
                        funcCodes.addAll(functionSet.stream().map(SysFunction::getPathUrl).collect(Collectors.toSet()));
                    }
                }

                //实例化
                permissionInfo = new SimplePermissionInfo(roleNames, funcCodes);
            }
        }

        return permissionInfo;
    }

    /**
     * 自定义从表单的验证信息获取数据库中正确的用户主体信息
     */
    @Override
    protected PrincipalInfo doGetPrincipalInfo(ValidatedInfo validatedInfo) throws AuthenticationException {
        //已知是“用户名+密码”的登录模式
        UsernamePasswordValidatedInfo usernamePasswordValidatedInfo = (UsernamePasswordValidatedInfo) validatedInfo;

        SysUser sysUser = sysUserMapper.selectByUsername(usernamePasswordValidatedInfo.getSubject());

        return new SimplePrincipalInfo(sysUser, sysUser.getUsername(), sysUser.getPassword());
    }

    /**
     * <p>提示：在修改用户主体信息、角色、权限等接口时，需要手动调用此方法清空缓存的PrincipalInfo和PermissionInfo</p>
     */
    protected void clearCache() {
        //1. 获取本次请求实例
        Access access = SecurityUtils.getAccess();
        //2. 获取PrincipalInfo
        PrincipalInfo principalInfo = access.getPrincipalInfo();
        //3. 清理缓存
        super.doClearCache(principalInfo);
    }

}
```

#### （2）添加`easylimit`框架的配置： ####

```java
package cn.zifangsky.easylimit.example.config;

import cn.zifangsky.easylimit.DefaultWebSecurityManager;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.cache.impl.DefaultRedisCache;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.example.easylimit.CustomRealm;
import cn.zifangsky.easylimit.example.mapper.SysFunctionMapper;
import cn.zifangsky.easylimit.example.mapper.SysRoleMapper;
import cn.zifangsky.easylimit.example.mapper.SysUserMapper;
import cn.zifangsky.easylimit.filter.impl.support.DefaultFilterEnums;
import cn.zifangsky.easylimit.filter.impl.support.FilterRegistrationFactoryBean;
import cn.zifangsky.easylimit.permission.aop.PermissionsAnnotationAdvisor;
import cn.zifangsky.easylimit.realm.Realm;
import cn.zifangsky.easylimit.session.SessionDAO;
import cn.zifangsky.easylimit.session.SessionIdFactory;
import cn.zifangsky.easylimit.session.SessionManager;
import cn.zifangsky.easylimit.session.impl.AbstractWebSessionManager;
import cn.zifangsky.easylimit.session.impl.MemorySessionDAO;
import cn.zifangsky.easylimit.session.impl.support.CookieWebSessionManager;
import cn.zifangsky.easylimit.session.impl.support.RandomCharacterSessionIdFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * EasyLimit框架的配置
 *
 * @author zifangsky
 * @date 2019/5/28
 * @since 1.0.0
 */
@Configuration
public class EasyLimitConfig {

    /**
     * 配置缓存
     */
    @Bean
    public Cache cache(RedisTemplate<String, Object> redisTemplate){
        return new DefaultRedisCache(redisTemplate);
    }

    /**
     * 配置Realm
     */
    @Bean
    public Realm realm(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysFunctionMapper sysFunctionMapper, Cache cache){
        CustomRealm realm = new CustomRealm(sysUserMapper, sysRoleMapper, sysFunctionMapper);
        //缓存主体信息
        realm.setEnablePrincipalInfoCache(true);
        realm.setPrincipalInfoCache(cache);

        //缓存角色、权限信息
        realm.setEnablePermissionInfoCache(true);
        realm.setPermissionInfoCache(cache);

        return realm;
    }

    /**
     * 配置Session的存储方式
     */
    @Bean
    public SessionDAO sessionDAO(Cache cache){
        return new MemorySessionDAO();
    }

    /**
     * 配置session管理器
     */
    @Bean
    public AbstractWebSessionManager sessionManager(SessionDAO sessionDAO){
//        CookieInfo cookieInfo = new CookieInfo("custom_session_id");
        AbstractWebSessionManager sessionManager = new CookieWebSessionManager(/*cookieInfo*/);
        sessionManager.setSessionDAO(sessionDAO);

        //设置session超时时间为1小时
        sessionManager.setGlobalTimeout(1L);
        sessionManager.setGlobalTimeoutChronoUnit(ChronoUnit.HOURS);

        //设置定时校验的时间为2分钟
        sessionManager.setSessionValidationInterval(2L);
        sessionManager.setSessionValidationUnit(TimeUnit.MINUTES);

        //设置sessionId的生成方式
//        SessionIdFactory sessionIdFactory = new SnowFlakeSessionIdFactory(1L, 1L);
        SessionIdFactory sessionIdFactory = new RandomCharacterSessionIdFactory();
        sessionManager.setSessionIdFactory(sessionIdFactory);

        return sessionManager;
    }

    /**
     * 认证、权限、session等管理的入口
     */
    @Bean
    public SecurityManager securityManager(Realm realm, SessionManager sessionManager){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm, sessionManager);
        //踢出当前用户的旧会话
        securityManager.setKickOutOldSessions(true);

        return securityManager;
    }

    /**
     * 将filter添加到Spring管理
     */
    @Bean
    public FilterRegistrationFactoryBean filterRegistrationFactoryBean(SecurityManager securityManager){
        //添加指定路径的权限校验
        LinkedHashMap<String, String[]> patternPathFilterMap = new LinkedHashMap<>();
        patternPathFilterMap.put("/css/**", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        patternPathFilterMap.put("/layui/**", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        patternPathFilterMap.put("/index.html", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        patternPathFilterMap.put("/test/greeting", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
//        patternPathFilterMap.put("/test/selectByUsername", new String[]{"perms[/aaa/bbb]"});
        //其他路径需要登录才能访问
        patternPathFilterMap.put("/**/*.html", new String[]{DefaultFilterEnums.LOGIN.getFilterName()});

        FilterRegistrationFactoryBean factoryBean = new FilterRegistrationFactoryBean(ProjectModeEnums.DEFAULT, securityManager, patternPathFilterMap);

        //设置几个登录、未授权等相关URL
        factoryBean.setLoginUrl("/login.html");
        factoryBean.setLoginCheckUrl("/check");
        factoryBean.setUnauthorizedUrl("/error.html");

        return factoryBean;
    }

    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("filterRegistrationFactoryBean");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    /**
     * 添加对权限注解的支持
     */
    @Bean
    public PermissionsAnnotationAdvisor permissionsAnnotationAdvisor(){
        return new PermissionsAnnotationAdvisor("execution(* cn.zifangsky..controller..*.*(..))");
    }

}
```

#### （3）添加测试代码： ####

**登录注销相关示例：**

```java
/**
 * 登录验证
 * @author zifangsky
 * @date 2019/5/29 13:23
 * @since 1.0.0
 * @return java.util.Map<java.lang.String,java.lang.Object>
 */
@PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Map<String,Object> check(HttpServletRequest request){
    Map<String,Object> result = new HashMap<>(4);
    result.put("code",500);

    try {
        //用户名
        String username = request.getParameter("username");
        //密码
        String password = request.getParameter("password");
        //获取本次请求实例
        Access access = SecurityUtils.getAccess();

        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            result.put("msg","请求参数不能为空！");
            return result;
        }else{
            logger.debug(MessageFormat.format("用户[{0}]正在请求登录", username));

            //设置验证信息
            ValidatedInfo validatedInfo = new UsernamePasswordValidatedInfo(username, password, EncryptionTypeEnums.Sha256Crypt);

            //1. 登录验证
            access.login(validatedInfo);
        }

        Session session = access.getSession();

        //2. 返回给页面的数据
        //登录成功之后的回调地址
        String redirectUrl = (String) session.getAttribute(cn.zifangsky.easylimit.common.Constants.SAVED_SOURCE_URL_NAME);
        session.removeAttribute(cn.zifangsky.easylimit.common.Constants.SAVED_SOURCE_URL_NAME);

        if(StringUtils.isNoneBlank(redirectUrl)){
            result.put("redirect_uri", redirectUrl);
        }
        result.put("code",200);
    }catch (Exception e){
        result.put("code", 500);
        result.put("msg", "登录失败，用户名或密码错误！");

        logger.error("登录失败",e);
    }

    return result;
}

/**
 * 退出登录
 * @author zifangsky
 * @date 2019/5/29 17:44
 * @since 1.0.0
 * @return java.util.Map<java.lang.String,java.lang.Object>
 */
@PostMapping(value = "/logout.html", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Map<String,Object> logout(HttpServletRequest request){
    Map<String,Object> result = new HashMap<>(1);

    Access access = SecurityUtils.getAccess();
    SysUser user = (SysUser) access.getPrincipalInfo().getPrincipal();

    if(user != null){
        logger.debug(MessageFormat.format("用户[{0}]正在退出登录", user.getUsername()));
    }

    try {
        //1. 退出登录
        access.logout();

        //2. 返回状态码
        result.put("code", 200);
    }catch (Exception e){
        result.put("code",500);
    }

    return result;
}
```

**权限校验注解示例：**

目前默认提供了以下三个权限校验注解（可扩展）：

- @RequiresLogin
- @RequiresPermissions
- @RequiresRoles

```java
@ResponseBody
@RequiresPermissions("/aaa/bbb")
@RequestMapping(value = "/selectByUsername", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public SysUser selectByUsername(String username) {
    return testService.selectByUsername(username);
}
```



## 前后端分离项目开发模式 ##

#### （1）自定义登录方式，以及角色、权限相关信息的获取方式： ####

这一步跟上面一样。



#### （2）添加`easylimit`框架的配置： ####

在`easylimit`框架的配置过程中，与`MVC`项目开发模式相比不同的地方有三处。第一是需要将`SessionManager`设置为`TokenWebSessionManager`及其子类；第二是需要将`SecurityManager`设置为`TokenWebSecurityManager`及其子类；第三是需要设置当前项目模式为`ProjectModeEnums.TOKEN`。

```java
package cn.zifangsky.easylimit.token.example.config;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.TokenWebSecurityManager;
import cn.zifangsky.easylimit.cache.Cache;
import cn.zifangsky.easylimit.cache.impl.DefaultRedisCache;
import cn.zifangsky.easylimit.enums.ProjectModeEnums;
import cn.zifangsky.easylimit.filter.impl.support.DefaultFilterEnums;
import cn.zifangsky.easylimit.filter.impl.support.FilterRegistrationFactoryBean;
import cn.zifangsky.easylimit.permission.aop.PermissionsAnnotationAdvisor;
import cn.zifangsky.easylimit.realm.Realm;
import cn.zifangsky.easylimit.session.SessionDAO;
import cn.zifangsky.easylimit.session.SessionIdFactory;
import cn.zifangsky.easylimit.session.TokenDAO;
import cn.zifangsky.easylimit.session.impl.DefaultTokenOperateResolver;
import cn.zifangsky.easylimit.session.impl.MemorySessionDAO;
import cn.zifangsky.easylimit.session.impl.support.DefaultCacheTokenDAO;
import cn.zifangsky.easylimit.session.impl.support.RandomCharacterSessionIdFactory;
import cn.zifangsky.easylimit.session.impl.support.TokenInfo;
import cn.zifangsky.easylimit.session.impl.support.TokenWebSessionManager;
import cn.zifangsky.easylimit.token.example.easylimit.CustomRealm;
import cn.zifangsky.easylimit.token.example.mapper.SysFunctionMapper;
import cn.zifangsky.easylimit.token.example.mapper.SysRoleMapper;
import cn.zifangsky.easylimit.token.example.mapper.SysUserMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * EasyLimit框架的配置
 *
 * @author zifangsky
 * @date 2019/5/28
 * @since 1.0.0
 */
@Configuration
public class EasyLimitConfig {

    /**
     * 配置缓存
     */
    @Bean
    public Cache cache(RedisTemplate<String, Object> redisTemplate){
        return new DefaultRedisCache(redisTemplate);
    }

    /**
     * 配置Realm
     */
    @Bean
    public Realm realm(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper, SysFunctionMapper sysFunctionMapper, Cache cache){
        CustomRealm realm = new CustomRealm(sysUserMapper, sysRoleMapper, sysFunctionMapper);
        //缓存主体信息
        realm.setEnablePrincipalInfoCache(true);
        realm.setPrincipalInfoCache(cache);

        //缓存角色、权限信息
        realm.setEnablePermissionInfoCache(true);
        realm.setPermissionInfoCache(cache);

        return realm;
    }

    /**
     * 配置Session的存储方式
     */
    @Bean
    public SessionDAO sessionDAO(Cache cache){
        return new MemorySessionDAO();
    }

    /**
     * 配置Token的存储方式
     */
    @Bean
    public TokenDAO tokenDAO(Cache cache){
        return new DefaultCacheTokenDAO(cache);
    }

    /**
     * 配置session管理器
     */
    @Bean
    public TokenWebSessionManager sessionManager(SessionDAO sessionDAO, TokenDAO tokenDAO){
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccessTokenTimeout(2L);
        tokenInfo.setAccessTokenTimeoutUnit(ChronoUnit.MINUTES);
        tokenInfo.setRefreshTokenTimeout(1L);
        tokenInfo.setRefreshTokenTimeoutUnit(ChronoUnit.DAYS);

        //创建基于Token的session管理器
        TokenWebSessionManager sessionManager = new TokenWebSessionManager(tokenInfo,new DefaultTokenOperateResolver(), tokenDAO);
        sessionManager.setSessionDAO(sessionDAO);

        //设置定时校验的时间为3分钟
        sessionManager.setSessionValidationInterval(3L);
        sessionManager.setSessionValidationUnit(TimeUnit.MINUTES);

        //设置sessionId的生成方式
        SessionIdFactory sessionIdFactory = new RandomCharacterSessionIdFactory();
        sessionManager.setSessionIdFactory(sessionIdFactory);

        return sessionManager;
    }

    /**
     * 认证、权限、session等管理的入口
     */
    @Bean
    public SecurityManager securityManager(Realm realm, TokenWebSessionManager sessionManager){
        return new TokenWebSecurityManager(realm, sessionManager);
    }

    /**
     * 将filter添加到Spring管理
     */
    @Bean
    public FilterRegistrationFactoryBean filterRegistrationFactoryBean(SecurityManager securityManager){
        //添加指定路径的权限校验
        LinkedHashMap<String, String[]> patternPathFilterMap = new LinkedHashMap<>();
        patternPathFilterMap.put("/css/**", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        patternPathFilterMap.put("/layui/**", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
//        patternPathFilterMap.put("/test/greeting", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
        patternPathFilterMap.put("/refreshToken", new String[]{DefaultFilterEnums.ANONYMOUS.getFilterName()});
//        patternPathFilterMap.put("/test/selectByUsername", new String[]{"perms[/aaa/bbb]"});
        //其他路径需要登录才能访问
        patternPathFilterMap.put("/**", new String[]{DefaultFilterEnums.LOGIN.getFilterName()});

        FilterRegistrationFactoryBean factoryBean = new FilterRegistrationFactoryBean(ProjectModeEnums.TOKEN, securityManager, patternPathFilterMap);

        //设置几个登录、未授权等相关URL
        factoryBean.setLoginCheckUrl("/login");

        return factoryBean;
    }

    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("filterRegistrationFactoryBean");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    /**
     * 添加对权限注解的支持
     */
    @Bean
    public PermissionsAnnotationAdvisor permissionsAnnotationAdvisor(){
        return new PermissionsAnnotationAdvisor("execution(* cn.zifangsky..controller..*.*(..))");
    }

}
```

#### （3）添加测试代码：

**登录注销相关示例：**

```java
/**
 * 登录验证
 * @author zifangsky
 * @date 2019/5/29 13:23
 * @since 1.0.0
 * @return java.util.Map<java.lang.String,java.lang.Object>
 */
@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Map<String,Object> check(HttpServletRequest request){
    Map<String,Object> result = new HashMap<>(4);
    result.put("code",500);

    try {
        //用户名
        String username = request.getParameter("username");
        //密码
        String password = request.getParameter("password");
        //获取本次请求实例
        ExposedTokenAccess access = (ExposedTokenAccess) SecurityUtils.getAccess();

        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            result.put("msg","请求参数不能为空！");
            return result;
        }else{
            logger.debug(MessageFormat.format("用户[{0}]正在请求登录", username));

            //设置验证信息
            ValidatedInfo validatedInfo = new UsernamePasswordValidatedInfo(username, password, EncryptionTypeEnums.Sha256Crypt);

            //1. 登录验证
            access.login(validatedInfo);
        }

        //2. 获取Access Token和Refresh Token
        SimpleAccessToken accessToken = access.getAccessToken();
        SimpleRefreshToken refreshToken = access.getRefreshToken();

        //3. 返回给页面的数据
        result.put("code",200);
        result.put("access_token", accessToken.getAccessToken());
        result.put("refresh_token", refreshToken.getRefreshToken());
        result.put("expires_in", accessToken.getExpiresIn());
//            result.put("user_info", accessToken.getPrincipalInfo().getPrincipal());
    }catch (Exception e){
        result.put("code", 500);
        result.put("msg", "登录失败，用户名或密码错误！");

        logger.error("登录失败",e);
    }

    return result;
}


/**
 * 退出登录
 * @author zifangsky
 * @date 2019/5/29 17:44
 * @since 1.0.0
 * @return java.util.Map<java.lang.String,java.lang.Object>
 */
@PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Map<String,Object> logout(HttpServletRequest request){
    Map<String,Object> result = new HashMap<>(1);

    Access access = SecurityUtils.getAccess();
    SysUser user = (SysUser) access.getPrincipalInfo().getPrincipal();

    if(user != null){
        logger.debug(MessageFormat.format("用户[{0}]正在退出登录", user.getUsername()));
    }

    try {
        //1. 退出登录
        access.logout();

        //2. 返回状态码
        result.put("code", 200);
    }catch (Exception e){
        result.put("code",500);
    }

    return result;
}
```

**刷新`Access Token`相关示例：**

```java
/**
 * 刷新Access Token
 * @author zifangsky
 * @date 2019/5/29 13:23
 * @since 1.0.0
 * @return java.util.Map<java.lang.String,java.lang.Object>
 */
@RequestMapping(value = "/refreshToken", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Map<String,Object> refreshAccessToken(HttpServletRequest request){
    Map<String,Object> result = new HashMap<>(4);
    result.put("code",500);

    try {
        //Refresh Token
        String refreshTokenStr = request.getParameter("refresh_token");
        //获取本次请求实例
        ExposedTokenAccess access = (ExposedTokenAccess) SecurityUtils.getAccess();

        //1. 刷新Access Token
        SimpleAccessToken newAccessToken = access.refreshAccessToken(refreshTokenStr);

        //2. 返回给页面的数据
        result.put("code",200);
        result.put("access_token", newAccessToken.getAccessToken());
        result.put("expires_in", newAccessToken.getExpiresIn());
        result.put("refresh_token", refreshTokenStr);
    }catch (Exception e){
        result.put("code", 500);
        result.put("msg", "Refresh Token不可用！");

        logger.error("Refresh Token不可用",e);
    }

    return result;
}
```

**权限校验注解示例：**

基本用法跟MVC项目开发模式一样，但是不同的地方有两点。第一是请求接口的时候需要传递`Access Token`，默认支持以下三种方式传参（规则定义在`cn/zifangsky/easylimit/session/impl/support/TokenWebSessionManager.java`的`getAccessTokenFromRequest()`方法）：

- `url`参数或者`form-data`参数中携带了`Access Token`（其名称在上述配置的`TokenInfo`类中定义）
- `header`参数中携带了`Access Token`（其名称同上）
- `header`参数中携带了`Access Token`（其名称为`Authorization`）

![AccessToken传参示例](images/AccessToken传参示例.png)



第二是在没有要求的角色/权限时，系统只会返回对应的状态码和提示信息，而不会像在MVC项目开发模式那样直接重定向到登录页面。示例接口以及返回的错误提示如下所示：

```java
@ResponseBody
@RequiresPermissions("/aaa/bbb")
@RequestMapping(value = "/selectByUsername", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public SysUser selectByUsername(String username) {
    return testService.selectByUsername(username);
}
```

错误提示信息：

```json
{
    "name": "no_permissions",
    "msg": "您当前没有权限访问该地址！",
    "code": 403
}
```

------

更多用法示例，可以参考这两个`example`项目：

- MVC项目开发模式的示例项目：[https://github.com/zifangsky/easylimit-example](https://github.com/zifangsky/easylimit-example)
- 前后端分离项目开发模式的示例项目：[https://github.com/zifangsky/easylimit-token-example](https://github.com/zifangsky/easylimit-token-example)