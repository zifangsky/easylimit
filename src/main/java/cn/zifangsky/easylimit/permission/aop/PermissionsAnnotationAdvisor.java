package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.permission.annotation.RequiresLogin;
import cn.zifangsky.easylimit.permission.annotation.RequiresPermissions;
import cn.zifangsky.easylimit.permission.annotation.RequiresRoles;
import org.aopalliance.aop.Advice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限注解处理的AOP通知
 *
 * @author zifangsky
 * @date 2019/6/20
 * @since 1.0.0
 */
public class PermissionsAnnotationAdvisor extends StaticMethodMatcherPointcutAdvisor {
    private static final long serialVersionUID = 3336828123978825653L;

    /**
     * 权限注解的列表
     */
    private final List<Class<? extends Annotation>> ANNOTATION_LIST;

    /**
     * AOP切面的表达式
     */
    private String aopExpression;

    public PermissionsAnnotationAdvisor(String aopExpression) {
        this(aopExpression, new DefaultAnnotationMethodInterceptor(), new ArrayList<>());
    }

    public PermissionsAnnotationAdvisor(String aopExpression, List<Class<? extends Annotation>> annotationList) {
        this(aopExpression, new DefaultAnnotationMethodInterceptor(), annotationList);
    }

    public PermissionsAnnotationAdvisor(String aopExpression, Advice advice, List<Class<? extends Annotation>> annotationList) {
        if(aopExpression == null){
            throw new IllegalArgumentException("Parameter aopExpression cannot be empty.");
        }
        if(advice == null){
            throw new IllegalArgumentException("Parameter advice cannot be empty.");
        }
        if(annotationList == null){
            throw new IllegalArgumentException("Parameter annotationList cannot be empty.");
        }

        this.aopExpression = aopExpression;
        this.ANNOTATION_LIST = annotationList;

        this.setAdvice(advice);
        //添加几个默认的权限注解
        this.addDefaultAnnotations();
        //设置默认的ClassFilter
        this.setDefaultClassFilter();
    }

    /**
     * 设置默认的ClassFilter
     */
    protected void setDefaultClassFilter(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(this.aopExpression);

        this.setClassFilter(pointcut);
    }

    /**
     * 添加几个默认的权限注解
     */
    protected void addDefaultAnnotations(){
        this.ANNOTATION_LIST.add(RequiresLogin.class);
        this.ANNOTATION_LIST.add(RequiresRoles.class);
        this.ANNOTATION_LIST.add(RequiresPermissions.class);
    }


    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Method tmpMethod = method;

        if(this.isPermissionsAnnotation(tmpMethod)){
            return true;
        }

        //查找实现类中是否存在权限注解
        if(targetClass != null){
            try {
                tmpMethod = targetClass.getMethod(tmpMethod.getName(), tmpMethod.getParameterTypes());
                return this.isPermissionsAnnotation(tmpMethod) || this.isPermissionsAnnotation(targetClass);
            } catch (NoSuchMethodException e) {
                //ignore
            }
        }

        return false;
    }

    /**
     * 查找是否存在权限注解
     */
    private boolean isPermissionsAnnotation(Method method){
        for(Class<? extends Annotation> clazz : this.ANNOTATION_LIST){
            Annotation annotation = AnnotationUtils.findAnnotation(method, clazz);
            if (annotation != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * 查找是否存在权限注解
     */
    private boolean isPermissionsAnnotation(Class<?> targetClazz){
        for(Class<? extends Annotation> clazz : this.ANNOTATION_LIST){
            Annotation annotation = AnnotationUtils.findAnnotation(targetClazz, clazz);
            if (annotation != null) {
                return true;
            }
        }

        return false;
    }

}
