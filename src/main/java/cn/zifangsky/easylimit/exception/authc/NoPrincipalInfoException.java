package cn.zifangsky.easylimit.exception.authc;

/**
 * 没有正确的用户主体信息异常
 *
 * @author zifangsky
 * @date 2019/4/11
 * @since 1.0.0
 */
public class NoPrincipalInfoException extends AuthenticationException {
    private static final long serialVersionUID = 8898016821504044651L;

    public NoPrincipalInfoException() {
        super();
    }

    public NoPrincipalInfoException(String message) {
        super(message);
    }

    public NoPrincipalInfoException(Throwable cause) {
        super(cause);
    }

    public NoPrincipalInfoException(String message, Throwable cause) {
        super(message, cause);
    }

}
