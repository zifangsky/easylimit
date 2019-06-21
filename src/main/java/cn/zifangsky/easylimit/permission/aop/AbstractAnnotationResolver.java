package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.access.Access;
import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import cn.zifangsky.easylimit.utils.SecurityUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注解相关AOP处理器
 *
 * @author zifangsky
 * @date 2019/6/18
 * @since 1.0.0
 */
public abstract class AbstractAnnotationResolver {
    /**
     * 注解类型
     */
    protected Class<? extends Annotation> annotationClass;

    public AbstractAnnotationResolver(Class<? extends Annotation> annotationClass) {
        if(annotationClass == null){
            throw new IllegalArgumentException("Parameter annotationClass cannot be empty.");
        }

        this.annotationClass = annotationClass;
    }

    /**
     * 校验角色、权限
     * @author zifangsky
     * @date 2019/6/19 10:26
     * @since 1.0.0
     * @param annotation 权限注解
     */
    protected abstract void assertPermission(Annotation annotation) throws AuthenticationException;

    /**
     * 校验角色、权限
     * @author zifangsky
     * @date 2019/6/19 10:26
     * @since 1.0.0
     * @param invocation MethodInvocation
     */
    public void assertPermission(MethodInvocation invocation) throws AuthenticationException{
        this.assertPermission(this.getAnnotation(invocation));
    }

    /**
     * 返回当前处理器是否支持
     * @author zifangsky
     * @date 2019/6/19 11:34
     * @since 1.0.0
     * @param invocation MethodInvocation
     * @return boolean
     */
    public boolean support(MethodInvocation invocation){
        return this.getAnnotation(invocation) != null;
    }

    /**
     * 获取注解数据
     */
    protected Annotation getAnnotation(MethodInvocation invocation) {
        return this.getAnnotation(invocation, annotationClass);
    }

    /**
     * 获取注解数据
     */
    protected Annotation getAnnotation(MethodInvocation invocation, Class<? extends Annotation> clazz) {
        Method method = invocation.getMethod();

        //1. 查找注解
        Annotation annotation = AnnotationUtils.findAnnotation(method, clazz);
        if (annotation != null) {
            return annotation;
        }

        //2. 尝试从实现类中查找注解
        Class<?> targetClass = invocation.getThis().getClass();
        method = ClassUtils.getMostSpecificMethod(method, targetClass);
        annotation = AnnotationUtils.findAnnotation(method, clazz);

        if (annotation != null) {
            return annotation;
        }

        return AnnotationUtils.findAnnotation(invocation.getThis().getClass(), clazz);
    }

    /**
     * 获取请求实例
     */
    protected Access getAccess() {
        return SecurityUtils.getAccess();
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }
}
