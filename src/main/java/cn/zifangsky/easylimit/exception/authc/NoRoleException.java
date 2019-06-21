package cn.zifangsky.easylimit.exception.authc;

/**
 * 没有角色异常
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class NoRoleException extends AuthenticationException {
    private static final long serialVersionUID = 1636338538923061407L;

    public NoRoleException() {
        super();
    }

    public NoRoleException(String message) {
        super(message);
    }

    public NoRoleException(Throwable cause) {
        super(cause);
    }

    public NoRoleException(String message, Throwable cause) {
        super(message, cause);
    }

}
