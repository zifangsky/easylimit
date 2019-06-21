package cn.zifangsky.easylimit.permission.aop;

import cn.zifangsky.easylimit.exception.authc.AuthenticationException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现的处理权限注解的{@link MethodInterceptor}
 *
 * @author zifangsky
 * @date 2019/6/19
 * @since 1.0.0
 */
public class DefaultAnnotationMethodInterceptor extends AbstractAnnotationMethodInterceptor {

    protected List<AbstractAnnotationResolver> annotationResolverList;

    public DefaultAnnotationMethodInterceptor() {
        this(new ArrayList<>());
    }

    public DefaultAnnotationMethodInterceptor(List<AbstractAnnotationResolver> annotationResolverList) {
        if(annotationResolverList == null){
            throw new IllegalArgumentException("Parameter annotationResolverList cannot be empty.");
        }

        this.annotationResolverList = annotationResolverList;
        //添加几个默认的注解处理器
        this.addDefaultAnnotationResolvers();
    }

    @Override
    protected void assertPermission(MethodInvocation invocation) throws AuthenticationException {
        if(this.annotationResolverList.size() > 0){
            for(AbstractAnnotationResolver resolver : this.annotationResolverList){
                if(resolver.support(invocation)){
                    resolver.assertPermission(invocation);
                }
            }
        }
    }

    /**
     * 添加几个默认的注解处理器
     */
    protected void addDefaultAnnotationResolvers(){
        this.annotationResolverList.add(new LoginAnnotationResolver());
        this.annotationResolverList.add(new RolesAnnotationResolver());
        this.annotationResolverList.add(new PermissionsAnnotationResolver());
    }

    public List<AbstractAnnotationResolver> getAnnotationResolverList() {
        return annotationResolverList;
    }

    public void setAnnotationResolverList(List<AbstractAnnotationResolver> annotationResolverList) {
        this.annotationResolverList = annotationResolverList;
    }
}
