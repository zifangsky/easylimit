package cn.zifangsky.easylimit.exception.authc;

/**
 * 没有权限异常
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class NoPermissionException extends AuthenticationException {
    private static final long serialVersionUID = 318520983615710875L;

    public NoPermissionException() {
        super();
    }

    public NoPermissionException(String message) {
        super(message);
    }

    public NoPermissionException(Throwable cause) {
        super(cause);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

}
