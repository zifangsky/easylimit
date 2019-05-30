package cn.zifangsky.easylimit.exception.authc;

/**
 * 没有角色、权限信息异常
 *
 * @author zifangsky
 * @date 2019/4/11
 * @since 1.0.0
 */
public class NoPermissionInfoException extends AuthenticationException {
    private static final long serialVersionUID = -2146910736024645704L;

    public NoPermissionInfoException() {
        super();
    }

    public NoPermissionInfoException(String message) {
        super(message);
    }

    public NoPermissionInfoException(Throwable cause) {
        super(cause);
    }

    public NoPermissionInfoException(String message, Throwable cause) {
        super(message, cause);
    }

}
