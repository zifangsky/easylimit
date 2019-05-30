package cn.zifangsky.easylimit.access.impl.support;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.BindOperator;

/**
 * 在过滤链执行之前，做一些绑定操作
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class AccessRunnable implements Runnable {
    /**
     * 绑定逻辑
     */
    private BindOperator bindOperator;

    /**
     * 实际的线程执行逻辑
     */
    private Runnable runnable;

    public AccessRunnable(Access access, Runnable runnable){
        this(new AccessBindOperator(access), runnable);
    }

    protected AccessRunnable(BindOperator bindOperator, Runnable runnable) {
        if (bindOperator == null){
            throw new IllegalArgumentException("Parameter bindOperator cannot be empty.");
        }
        if (runnable == null){
            throw new IllegalArgumentException("Parameter runnable cannot be empty.");
        }

        this.bindOperator = bindOperator;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            //1. 绑定
            this.bindOperator.bind();
            //2. 继续执行线程的逻辑
            this.doRun(this.runnable);
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
    protected void doRun(Runnable runnable){
        runnable.run();
    }
}
