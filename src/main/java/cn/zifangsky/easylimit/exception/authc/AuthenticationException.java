package cn.zifangsky.easylimit.exception.authc;

import cn.zifangsky.easylimit.exception.EasyLimitException;

/**
 * 登录认证相关的异常
 *
 * @author zifangsky
 * @date 2019/4/3
 * @since 1.0.0
 */
public class AuthenticationException extends EasyLimitException {
    private static final long serialVersionUID = 3878492919699820228L;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
