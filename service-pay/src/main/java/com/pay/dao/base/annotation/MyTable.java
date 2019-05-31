package com.pay.dao.base.annotation;

import java.lang.annotation.*;

/**
 * Table注解，应用于实体类上，用于表明实体类对应的表名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MyTable {

	/**
	 * 表名
	 * @return
	 */
	String table();

	/**
	 * 主键字段名称
	 * @return
	 */
	String id();

	/**
	 * 主键字段对应的表字段名称
	 * @return
	 */
	String idField();
}