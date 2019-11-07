package luckyclient.execution.dispose.actionkeyword;

/**
 * 动作关键字注解定义
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Action {
    String name() default "";
}
