package cn.zifangsky.easylimit.exception.session;

import cn.zifangsky.easylimit.exception.EasyLimitException;
import cn.zifangsky.easylimit.session.Session;

/**
 * {@link Session}相关异常
 *
 * @author zifangsky
 * @date 2019/3/23
 * @since 1.0.0
 */
public class SessionException extends EasyLimitException {
    private static final long serialVersionUID = -7315660463328340994L;

    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
