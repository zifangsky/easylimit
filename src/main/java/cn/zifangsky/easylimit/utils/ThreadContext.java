package cn.zifangsky.easylimit.utils;

import cn.zifangsky.easylimit.SecurityManager;
import cn.zifangsky.easylimit.access.Access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 在线程中保存一些私有数据
 *
 * @author zifangsky
 * @date 2019/4/2
 * @since 1.0.0
 */
public class ThreadContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadContext.class);

    /**
     * {@link SecurityManager}的key
     */
    private static final String SECURITY_MANAGER_KEY = ThreadContext.class.getName() + ":security_manager";

    /**
     * {@link Access}的key
     */
    private static final String ACCESS_KEY = ThreadContext.class.getName() + ":access";

    /**
     * 定义一个ThreadLocal
     */
    private static final ThreadLocal<Map<String, Object>> RESOURCES = new InheritableThreadLocal<>();

    /**
     * 初始化
     */
    private static void initialize() {
        if (RESOURCES.get() == null) {
            RESOURCES.set(new HashMap<>());
        }
    }

    /**
     * 获取保存的所有数据
     *
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author zifangsky
     * @date 2019/4/2 17:48
     * @since 1.0.0
     */
    public static Map<String, Object> getResources() {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> resources = RESOURCES.get();
        if (resources != null && resources.size() > 0) {
            result.putAll(resources);
        }

        return result;
    }

    /**
     * 获取指定的值
     *
     * @param key key
     * @return java.lang.Object
     * @author zifangsky
     * @date 2019/4/2 17:52
     * @since 1.0.0
     */
    public static Object get(String key) {
        Map<String, Object> resources = RESOURCES.get();
        return resources != null ? resources.get(key) : null;
    }

    /**
     * 存放指定键值对
     *
     * @param key   key
     * @param value value
     * @author zifangsky
     * @date 2019/4/2 17:59
     * @since 1.0.0
     */
    public static void put(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Parameter key cannot be empty.");
        }
        if (value == null) {
            remove(key);
        }

        initialize();
        RESOURCES.get().put(key, value);
    }

    /**
     * 移除指定键值对
     *
     * @author zifangsky
     * @date 2019/4/2 17:44
     * @since 1.0.0
     */
    public static void remove(String key) {
        Map<String, Object> resources = RESOURCES.get();
        if (resources != null) {
            resources.remove(key);
        }
    }

    /**
     * 移除所有
     *
     * @author zifangsky
     * @date 2019/4/2 17:44
     * @since 1.0.0
     */
    public static void remove() {
        RESOURCES.remove();
    }

    public static void setResources(Map<String, Object> newResources) {
        if (newResources != null && newResources.size() > 0) {
            initialize();
            RESOURCES.get().putAll(newResources);
        }
    }

    /**
     * 获取{@link SecurityManager}
     * @author zifangsky
     * @date 2019/4/4 14:30
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.SecurityManager
     */
    public static SecurityManager getSecurityManager() {
        return (SecurityManager) get(SECURITY_MANAGER_KEY);
    }

    /**
     * 将{@link SecurityManager}绑定到{@link ThreadLocal}
     * @author zifangsky
     * @date 2019/4/4 14:31
     * @since 1.0.0
     * @param securityManager securityManager
     */
    public static void bindSecurityManager(SecurityManager securityManager) {
        if (securityManager != null) {
            put(SECURITY_MANAGER_KEY, securityManager);
        }
    }

    /**
     * 将{@link SecurityManager}从{@link ThreadLocal}解绑
     * @author zifangsky
     * @date 2019/4/4 14:31
     * @since 1.0.0
     */
    public static void unbindSecurityManager() {
        remove(SECURITY_MANAGER_KEY);
    }

    /**
     * 获取{@link Access}
     * @author zifangsky
     * @date 2019/4/4 14:30
     * @since 1.0.0
     * @return cn.zifangsky.easylimit.access.Access
     */
    public static Access getAccess() {
        return (Access) get(ACCESS_KEY);
    }

    /**
     * 将{@link Access}绑定到{@link ThreadLocal}
     * @author zifangsky
     * @date 2019/4/4 14:31
     * @since 1.0.0
     * @param access access
     */
    public static void bindAccess(Access access) {
        if (access != null) {
            put(ACCESS_KEY, access);
        }
    }

    /**
     * 将{@link Access}从{@link ThreadLocal}解绑
     * @author zifangsky
     * @date 2019/4/4 14:31
     * @since 1.0.0
     */
    public static void unbindAccess() {
        remove(ACCESS_KEY);
    }
}
