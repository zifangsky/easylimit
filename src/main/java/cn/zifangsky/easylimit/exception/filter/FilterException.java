package cn.zifangsky.easylimit.exception.filter;

import cn.zifangsky.easylimit.exception.EasyLimitException;

/**
 * filter相关异常
 *
 * @author zifangsky
 * @date 2019/5/10
 * @since 1.0.0
 */
public class FilterException extends EasyLimitException {
    private static final long serialVersionUID = -3913958002519387906L;

    public FilterException() {
        super();
    }

    public FilterException(String message) {
        super(message);
    }

    public FilterException(Throwable cause) {
        super(cause);
    }

    public FilterException(String message, Throwable cause) {
        super(message, cause);
    }
}
