package cn.zifangsky.easylimit.exception.cache;

import cn.zifangsky.easylimit.exception.EasyLimitException;

/**
 * 缓存相关异常
 *
 * @author zifangsky
 * @date 2019/4/1
 * @since 1.0.0
 */
public class CacheException extends EasyLimitException {
    private static final long serialVersionUID = -8473442128035725315L;

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
