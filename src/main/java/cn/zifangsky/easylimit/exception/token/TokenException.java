package cn.zifangsky.easylimit.exception.token;

import cn.zifangsky.easylimit.exception.EasyLimitException;

/**
 * Token相关异常
 *
 * @author zifangsky
 * @date 2019/3/23
 * @since 1.0.0
 */
public class TokenException extends EasyLimitException {
    private static final long serialVersionUID = 6565813579906927611L;

    public TokenException() {
        super();
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(Throwable cause) {
        super(cause);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
