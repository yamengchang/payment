package com.omv.database.biz;

import com.omv.database.bean.*;
import com.omv.database.dynamicDB.*;
import org.springframework.beans.*;
import org.springframework.data.repository.*;
import org.springframework.util.*;

import java.io.*;
import java.util.*;

/**
 * Created by SJQ on 2017/2/10.
 */
@NoRepositoryBean
@TargetDataSource
public class BaseServiceImpl<T, ID extends Serializable> extends BaseBiz<T, ID> implements BaseService<T, ID> {
	@Override
	public T findOne(ID id) {
		return super.findOne(id);
	}

	@Override
	public PageModel findAll(PageModel pageModel) {
		return super.findAll(pageModel);
	}


	@Override
	public T save(T t) {
		return super.save(t);
	}

	@Override
	public List findAll() {
		return super.findAll();
	}

	@Override
	public List findAll(Collection ids) {
		return super.findAll(ids);
	}

	@Override
	public Collection<T> save(Collection<T> collection) {
		return super.save(collection);
	}

	public void deleteById(ID id) {
		super.deleteById(id);
	}

	@Override
	public T update(T t, ID id) {
		Assert.notNull(t, "参数不能为空");
		Assert.notNull(id, "id不能为空");

		T target = (T)findOne(id);
		Assert.notNull(target, "没有找到对应的数据");
		// 除去 D 的非空属性复制
		copyPropertiesIgnoreNull(t, target);

		return save(target);
	}

	@Override
	public T updateNotNull(T t, ID id) {
		Assert.notNull(t, "参数不能为空");
		Assert.notNull(id, "id不能为空");

		T target = (T)findOne(id);
		Assert.notNull(target, "没有找到对应的数据");
		// 除去 D 的非空属性复制
		copyPropertiesIgnore(t, target);
		return save(target);

	}

	/**
	 * 封装spring 的BeanUtils 的方法  除去为null 和""的属性赋值
	 * tdf
	 * @param src 源
	 * @param target 目标
	 */
	private static void copyPropertiesIgnoreNull(Object src, Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	/**
	 * 封装spring 的BeanUtils 的方法  除去为null 的属性赋值
	 * tdf
	 * @param src 源
	 * @param target 目标
	 */
	private static void copyPropertiesIgnore(Object src, Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyName(src));
	}

	/**
	 * 根据目标源，返回字段值为空的属性数组
	 * 通过反射,获取数据值为null或为"" 的属性，并以数组的形式返回
	 * @param source 源
	 * @return 空属性数组
	 */
	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> emptyNames = new HashSet<>(4);
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null || "".equals(srcValue)) {
				emptyNames.add(pd.getName());
			}
		}

		return emptyNames.toArray(new String[emptyNames.size()]);
	}


	/**
	 * 根据目标源，返回字段值为空的属性数组
	 * 通过反射,获取数据值为null  的属性，并以数组的形式返回
	 * @param source 源
	 * @return 空属性数组
	 */
	private static String[] getNullPropertyName(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> emptyNames = new HashSet<>(4);
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) {
				emptyNames.add(pd.getName());
			}
		}

		return emptyNames.toArray(new String[emptyNames.size()]);
	}

}
