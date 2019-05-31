package com.omv.database.dynamicDB;

import java.lang.annotation.*;

/**
 * Created by zwj on 2018/3/26.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value() default "";

}
