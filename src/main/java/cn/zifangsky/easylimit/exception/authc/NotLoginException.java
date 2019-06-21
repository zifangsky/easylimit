package cn.zifangsky.easylimit.exception.authc;

/**
 * 未登录异常
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class NotLoginException extends AuthenticationException {
    private static final long serialVersionUID = 1194440512415730823L;

    public NotLoginException() {
        super();
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(Throwable cause) {
        super(cause);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
