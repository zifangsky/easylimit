package cn.zifangsky.easylimit.access.impl.support;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.BindOperator;

import java.util.concurrent.Callable;

/**
 * 在过滤链执行之前，做一些绑定操作
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class AccessCallable<T> implements Callable<T> {
    /**
     * 绑定逻辑
     */
    private BindOperator bindOperator;

    /**
     * 实际的线程执行逻辑
     */
    private Callable<T> callable;

    public AccessCallable(Access access, Callable<T> callable){
        this(new AccessBindOperator(access), callable);
    }

    protected AccessCallable(BindOperator bindOperator, Callable<T> callable) {
        if (bindOperator == null){
            throw new IllegalArgumentException("Parameter bindOperator cannot be empty.");
        }
        if (callable == null){
            throw new IllegalArgumentException("Parameter callable cannot be empty.");
        }

        this.bindOperator = bindOperator;
        this.callable = callable;
    }

    @Override
    public T call() throws Exception {
        try {
            //1. 绑定
            this.bindOperator.bind();
            //2. 继续执行线程的逻辑
            return this.doCall(this.callable);
        }finally {
            //3. 恢复状态
            this.bindOperator.recovery();
        }
    }

    /**
     * 实际的线程执行逻辑
     * @author zifangsky
     * @date 2019/4/4 17:12
     * @since 1.0.0
     */
    protected T doCall(Callable<T> callable) throws Exception {
        return callable.call();
    }
}
