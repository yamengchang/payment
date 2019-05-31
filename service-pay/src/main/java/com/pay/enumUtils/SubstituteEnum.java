package com.pay.enumUtils;

public enum SubstituteEnum implements com.pay.dao.base.DatabaseEnum {

	/**
	 * 代收
	 */
	isSubstituteEnum {
		@Override
		public Integer getDBValue() {
			return 0;
		}
	},
	/**
	 * 代付
	 */
	notSubstituteEnum {
		@Override
		public Integer getDBValue() {
			return 1;
		}
	},

}
