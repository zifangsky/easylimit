package cn.zifangsky.easylimit.exception.session;

import cn.zifangsky.easylimit.session.Session;

/**
 * 不可用的{@link Session}
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class InvalidSessionException extends SessionException {
    private static final long serialVersionUID = -2429699711337999603L;

    public InvalidSessionException() {
        super();
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    public InvalidSessionException(Throwable cause) {
        super(cause);
    }

    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
