package cn.zifangsky.easylimit.exception.session;

import cn.zifangsky.easylimit.session.Session;

/**
 * <p>不可用的{@link Session}</p>
 * Note: 当应用已经禁用某个{@link Session}，但是在其他地方又尝试去访问这个{@link Session}，
 * 就会抛出{@link DisabledSessionException}这个异常。
 *
 * @author zifangsky
 * @date 2019/3/25
 * @since 1.0.0
 */
public class DisabledSessionException extends SessionException {
    private static final long serialVersionUID = 1388734905398240705L;

    public DisabledSessionException() {
        super();
    }

    public DisabledSessionException(String message) {
        super(message);
    }

    public DisabledSessionException(Throwable cause) {
        super(cause);
    }

    public DisabledSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
