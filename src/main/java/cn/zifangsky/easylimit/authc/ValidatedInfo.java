package cn.zifangsky.easylimit.authc;

import java.io.Serializable;

/**
 * 登录时来至外部的需要验证的信息
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public interface ValidatedInfo extends Serializable {
    /**
     * 获取主体，比如：用户名、手机号
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/4/3 18:18
     * @since 1.0.0
     */
    String getSubject();

    /**
     * 获取凭证信息，比如：密码、手机验证码
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/4/3 18:19
     * @since 1.0.0
     */
    String getCredentials();
}
