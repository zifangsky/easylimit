package cn.zifangsky.easylimit.access.impl.support;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.common.BindOperator;
import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import cn.zifangsky.easylimit.utils.ThreadContext;

import java.util.Map;

/**
 * 用于将{@link Access}和{@link SecurityManager}绑定到ThreadLocal
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class AccessBindOperator implements BindOperator {

    private Access access;
    private SecurityManager securityManager;
    /**
     * 绑定之前的数据，用于请求结束之后恢复之前状态
     * <P>Note: 目的是避免污染线程池中线程的{@link ThreadLocal}数据</P>
     */
    private Map<String, Object> originalResources;


    public AccessBindOperator(Access access) {
        if (access == null){
            throw new IllegalArgumentException("Parameter access cannot be empty.");
        }

        this.access = access;

        SecurityManager securityManager = access.getSecurityManager();

        //如果为空，则获取备份的SecurityManager
        if(securityManager == null){
            securityManager = SecurityUtils.getSecurityManager();
        }

        this.securityManager = securityManager;
    }

    /**
     * 实际的绑定操作
     * @author zifangsky
     * @date 2019/4/4 17:10
     * @since 1.0.0
     */
    @Override
    public void bind(){
        //1. 获取绑定之前的线程中的ThreadLocal数据
        this.originalResources = ThreadContext.getResources();

        //2.清空线程中的ThreadLocal数据
        ThreadContext.remove();

        //3. 绑定Access
        ThreadContext.bindAccess(this.access);
        //4. 绑定SecurityManager
        if(this.securityManager != null){
            ThreadContext.bindSecurityManager(this.securityManager);
        }
    }

    @Override
    public void recovery(){
        //1. 清空
        ThreadContext.remove();

        //2. 恢复
        if(this.originalResources != null && this.originalResources.size() > 0){
            ThreadContext.setResources(originalResources);
        }
    }

}
