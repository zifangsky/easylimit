package cn.zifangsky.easylimit.exception.authc;

/**
 * 密码错误异常
 *
 * @author zifangsky
 * @date 2019/4/11
 * @since 1.0.0
 */
public class IncorrectCredentialsException extends AuthenticationException {
    private static final long serialVersionUID = -3123740567200829829L;

    public IncorrectCredentialsException() {
        super();
    }

    public IncorrectCredentialsException(String message) {
        super(message);
    }

    public IncorrectCredentialsException(Throwable cause) {
        super(cause);
    }

    public IncorrectCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

}
