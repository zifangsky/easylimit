package cn.zifangsky.easylimit.exception.session;

import cn.zifangsky.easylimit.session.Session;

/**
 * 未知的{@link Session}
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class UnknownSessionException extends InvalidSessionException {
    private static final long serialVersionUID = -5971286828869438774L;

    public UnknownSessionException() {
        super();
    }

    public UnknownSessionException(String message) {
        super(message);
    }

    public UnknownSessionException(Throwable cause) {
        super(cause);
    }

    public UnknownSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
