package cn.zifangsky.easylimit.exception.session;

import cn.zifangsky.easylimit.session.Session;

/**
 * 过期的{@link Session}
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class ExpiredSessionException extends InvalidSessionException {
    private static final long serialVersionUID = 7124119918547850066L;

    public ExpiredSessionException() {
        super();
    }

    public ExpiredSessionException(String message) {
        super(message);
    }

    public ExpiredSessionException(Throwable cause) {
        super(cause);
    }

    public ExpiredSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
