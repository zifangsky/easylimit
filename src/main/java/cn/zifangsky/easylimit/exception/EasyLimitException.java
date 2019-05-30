package cn.zifangsky.easylimit.exception;

/**
 * easylimit项目的通用异常
 *
 * @author zifangsky
 * @date 2019/3/23
 * @since 1.0.0
 */
public class EasyLimitException extends RuntimeException {
    private static final long serialVersionUID = 7919839373220909439L;

    public EasyLimitException() {
    }

    public EasyLimitException(String message) {
        super(message);
    }

    public EasyLimitException(Throwable cause) {
        super(cause);
    }

    public EasyLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
