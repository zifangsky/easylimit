package cn.zifangsky.easylimit.utils;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.exception.EasyLimitException;
import cn.zifangsky.easylimit.SecurityManager;

/**
 * 用于静态获取{@link SecurityManager}
 *
 * @author zifangsky
 * @date 2019/4/4
 * @since 1.0.0
 */
public class SecurityUtils {
    /**
     * 静态保存一份{@link SecurityManager}，用于备份
     */
    private static SecurityManager securityManager;

    public static SecurityManager getSecurityManager() {
        //1. 获取ThreadLocal中的实例
        SecurityManager threadLocalRecord = ThreadContext.getSecurityManager();

        if(threadLocalRecord == null){
            //2. 如果为空，则获取备份的SecurityManager
            threadLocalRecord = SecurityUtils.securityManager;

            if(threadLocalRecord == null){
                //3. 如果还为空，则抛出异常
                throw new EasyLimitException("There is no SecurityManager available");
            }
        }

        return threadLocalRecord;
    }

    public static void setSecurityManager(SecurityManager securityManager) {
        SecurityUtils.securityManager = securityManager;
    }

    public static Access getAccess() {
        Access access = ThreadContext.getAccess();

        //如果ThreadLocal中没有，则存储一份默认的
        if(access == null){
            //TODO 默认的
        }
        return access;
    }
}
