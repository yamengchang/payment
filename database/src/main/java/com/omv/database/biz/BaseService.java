package com.omv.database.biz;


import com.omv.database.bean.*;

import java.util.*;

/**
 * Created by SJQ on 2017/2/9.
 */
public interface BaseService<T, ID> {
	T findOne(ID id);

	PageModel findAll(PageModel pageModel);

	T save(T t);

	List findAll();

	List findAll(Collection ids);

	Collection<T> save(Collection<T> collection);

	void deleteById(ID id);

	/**
	 * 更新操作。非空字段更新，不想更新的字段请设置成null或""
	 * @param t :实体
	 * @param id：id
	 * @return 修改后的值
	 */
	T update(T t, ID id);

	/**
	 * 更新操作。非空字段更新，不想更新的字段请设置成null
	 * @param t :实体
	 * @param id：id
	 * @return 修改后的值
	 */
	T updateNotNull(T t, ID id);
}
