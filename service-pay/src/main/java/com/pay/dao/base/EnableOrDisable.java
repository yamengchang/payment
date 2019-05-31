package com.pay.dao.base;


/**
 * 启用或禁用状态枚举
 * @author wanglidong
 */
public enum EnableOrDisable implements DatabaseEnum {

	/**
	 * 启用
	 */
	ENABLE {
		@Override
		public Integer getDBValue() {
			return 0;
		}
	},

	/**
	 * 禁用
	 */
	DISABLE {
		@Override
		public Integer getDBValue() {
			return 1;
		}
	};
	
	public static EnableOrDisable getByDBValue(Integer dbValue) {
		if (ENABLE.getDBValue().equals(dbValue)) {
			return ENABLE;
		}
		if (DISABLE.getDBValue().equals(dbValue)) {
			return DISABLE;
		}

		return null;
	}
}