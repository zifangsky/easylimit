package cn.zifangsky.easylimit.exception.token;

/**
 * 过期的Token
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class ExpiredTokenException extends TokenException {
    private static final long serialVersionUID = 2737561889191511199L;

    public ExpiredTokenException() {
        super();
    }

    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(Throwable cause) {
        super(cause);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
