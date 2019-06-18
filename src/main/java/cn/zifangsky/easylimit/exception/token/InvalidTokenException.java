package cn.zifangsky.easylimit.exception.token;

/**
 * 不可用的Token
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class InvalidTokenException extends TokenException {
    private static final long serialVersionUID = 6307461728213768148L;

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
