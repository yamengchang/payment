package com.pay.dao.base.annotation;

import java.lang.annotation.*;

/**
 * Persist注解，应用于字段上，用于表明该字段在新增和更新时的动作
 * @author 唐东峰
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MyPersist {

	public static final String PERSIST = "persist";

	public static final String UNINSERTABLE = "unInsertable";

	public static final String UNUPDATABLE = "unUpdatable";

	/**
	 * 持久化类型，有一下几个值：
	 * persist：表明该字段可插入,可更新
	 * unInsertable：表明该字段不可插入
	 * unUpdatable：表明该字段不可更新
	 * @return
	 */
	String[] value() default "persist";
}