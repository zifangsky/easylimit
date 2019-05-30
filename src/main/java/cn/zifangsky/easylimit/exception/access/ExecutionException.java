package cn.zifangsky.easylimit.exception.access;

import cn.zifangsky.easylimit.exception.EasyLimitException;

/**
 * 执行任务相关的异常
 *
 * @author zifangsky
 * @date 2019/4/8
 * @since 1.0.0
 */
public class ExecutionException extends EasyLimitException {
    private static final long serialVersionUID = 1322132876254013403L;

    public ExecutionException() {
        super();
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
