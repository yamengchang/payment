package com.pay.dao.base;


import java.util.*;

/**
 * 数据库操作基本接口
 * @param <T>
 */
public interface BaseDao<T> {
 /**
	 * 根据SQL更新对象
	 * @param sql
	 */
	void update(String sql);

	/**
	 * 根据SQL更新对象
	 * @param sql
	 * @param params 基于?占位符的参数列表
	 */
	void update(String sql, Object... params);

	/**
	 * 根据SQL更新对象
	 * @param sql
	 * @param alias 基于别名的参数集合
	 */
	void update(String sql, Map<String, Object> alias);
}