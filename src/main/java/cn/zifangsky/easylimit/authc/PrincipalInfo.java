package cn.zifangsky.easylimit.authc;

import java.io.Serializable;

/**
 * 用户主体
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public interface PrincipalInfo extends Serializable {

    /**
     * 获取账户名（必须唯一）
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/4/3 18:18
     * @since 1.0.0
     */
    String getAccount();

    /**
     * 获取用户主体
     *
     * @return T
     * @author zifangsky
     * @date 2019/4/3 17:58
     * @since 1.0.0
     */
    Object getPrincipal();

    /**
     * 获取密码
     *
     * @return java.lang.String
     * @author zifangsky
     * @date 2019/4/3 18:10
     * @since 1.0.0
     */
    String getPassword();
}
