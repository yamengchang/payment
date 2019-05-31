package com.sup.service;

import com.omv.mongo.util.*;
import com.sup.pojo.*;

import java.util.*;

/**
 * @author tdf
 * @date 2018-12-18 14:25
 **/
public interface IBaseMongoDbService<T extends BaseEntity> {
	/**
	 * 添加
	 * @param t :对象
	 */
	String save(T t);

	/**
	 * 批量插入
	 */
	String saveAll(List<T> list);

	/**
	 * 查询所有的
	 * @return :Page
	 */
	Page findAll(Map<String, Object> params);

	/**
	 * 根据id查询
	 * @param id：id
	 * @return : object
	 */
	Object findById(String id);

	/**
	 * 根据id删除
	 * @param id ：id
	 * @return ：long
	 */
	long delete(String id);

	/**
	 * 根据id更新  根据对象的非空字段更新
	 * @param t ：对象
	 * @return ：long
	 */
	long update(T t);

	/**
	 * 批量更新
	 */
	long batchUpdate(List<T> list);

	List<T> find(Map param);

	T findOne(Map param);
}
