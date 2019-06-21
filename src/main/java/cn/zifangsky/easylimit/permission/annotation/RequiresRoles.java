package cn.zifangsky.easylimit.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要的角色
 * @author zifangsky
 * @date 2019/6/18
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRoles {
    /**
     * 角色值
     */
    String[] value();

    /**
     * 逻辑运算：并且、或者
     */
    Logical logical() default Logical.AND;
}
