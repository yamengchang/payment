package com.pay.dao.base.annotation;

import java.lang.annotation.*;

/**
 * Column注解，应用实体字段上，用于定义该字段对应的表字段名称
 * 通常情况下，BaseDao会自动将实体类字段名转成表字段名，如果有特殊需求，可以使用该注解指定表字段名
 * @author 唐东峰
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MyColumn {

	/**
	 * 字段名
	 * @return
	 */
	String value();
}