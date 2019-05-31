package com.sup.util;

import org.apache.commons.logging.*;
import org.springframework.beans.*;
import org.springframework.util.*;

import java.util.*;

/**
 * @author tdf
 * @date 2018-12-21 15:50
 **/
public class BeanUtils {
	private static final Log logger = LogFactory.getLog(BeanUtils.class);

	/**
	 * 封装spring 的BeanUtils 的方法  除去为null 的属性赋值
	 * tdf
	 * @param src 源
	 * @param target 目标
	 */
	public static Object copyPropertiesIgnoreNull(Object src, Object target) {
		org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
		return target;
	}

	/**
	 * 封装spring 的BeanUtils 的方法
	 * tdf
	 * @param src 源
	 * @param target 目标
	 */
	public static Object copyProperties(Object src, Object target) {
		org.springframework.beans.BeanUtils.copyProperties(src, target);
		return target;
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
	 * 获取非空字段的，属性名，和对应的值
	 * @param map <属性名：对应的值>
	 * @param d DTO
	 * @return 非空属性的键值对   即：<属性：值>
	 */
	private Map<String, Object> getParam(Map<String, Object> map, Object d) {
		final BeanWrapper src = new BeanWrapperImpl(d);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue != null) {
				map.put(pd.getName(), srcValue);
			}
		}
		map.remove("class");
		return map;
	}

	/**
	 * 使用反射实例化对象
	 * @param clazz clazz
	 * @param <T> 要反射的model
	 * @return model通过反射获取的实例化对象
	 */
	public static <T> T newTclass(Class<T> clazz) {
		try {
			T t = clazz.newInstance();
			Assert.notNull(t, "通过反射获取实例化mode错误");
			return t;
		}catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			logger.error("通过反射获取实例化mode错误：" + e.getMessage());
			return null;
		}
	}
}
