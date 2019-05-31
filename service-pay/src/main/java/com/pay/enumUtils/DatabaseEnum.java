package com.pay.enumUtils;

/**
 * 枚举接口
 * 如果一个和数据库表对应的字段是枚举类型，该枚举需要实现本接口，BaseDaoImpl会基于该接口获取到对应存储在数据库中的值
 * @author 唐东峰
 */
public interface DatabaseEnum {

	/**
	 * 获取枚举对应的数据值
	 * @return
	 */
	Integer getDBValue();
}