# easylimit #

# 简介 #

`easylimit`框架是一个同时适用于传统`MVC`项目开发模式以及`前后端分离`项目开发模式的权限控制框架。需要注意的是，目前此框架仅限于在基于`Spring`的`Java Web`项目中运行，且主要依赖`spring-context`、`Jackson`、`Jedis`这几个组件。

## 功能特性 ##

在使用上，目前主要提供了以下功能特性：

- 同时支持`MVC`和`前后端分离`项目开发模式的权限控制
- 支持完整的`RBAC`权限控制
- 默认实现多种`session_id`生成方式，包括：`随机字符串`、`UUID`、`雪花算法`
- 默认实现多种`session`和`token`存储方式，包括：基于`ConcurrentHashMap`的内存存储、使用`Redis`等缓存存储
- 默认实现AOP切面，支持多种权限控制注解，包括：`@RequiresLogin`、`@RequiresPermissions`、`@RequiresRoles`
- 默认支持多种`Access Token`传参方式，且可以灵活扩展
- 默认实现“是否踢出当前用户的旧会话”的选项
- 默认实现多种登录登录方式、多种密码校验规则的简单接入。前者包括：“用户名+密码”登录、“手机号码+短信验证码”登录，后者包括：`Base64`、`Md5Hex`、`Sha256Hex`、`Sha512Hex`、`Md5Crypt`、`Sha256Crypt`等其他自定义密码加密/摘要方式
- 使用简单，可扩展性强
- 代码规范，注释完整，文档齐全，有助于通过源码学习其实现思路

## 开始使用 ##

使用方式及详细说明可以查看这个文档：[https://easylimit.zifangsky.cn/](https://easylimit.zifangsky.cn/)



## 鸣谢 ##

前几年的时候，我很喜欢`Apache Shiro`这个权限控制框架，不过后面慢慢地`Shiro`不再满足项目开发的实际需求，从而让我萌生了自己动手开发一个权限控制框架的想法。不过在`easylimit`这个框架的早期开发阶段，我参考了很多`Shiro`的设计理念以及源码实现，因此我在此表示对`Apache Shiro`开发组及社区由衷的感谢！

- Apache Shiro官网：[https://shiro.apache.org/](https://shiro.apache.org/)
- Apache Shiro源码：[https://github.com/apache/shiro](https://github.com/apache/shiro)



最后，我在设计基于`前后端分离`项目开发模式的权限控制时，也借鉴了一些`JSON Web Token (JWT) `的设计思想，在此也表示感谢！

