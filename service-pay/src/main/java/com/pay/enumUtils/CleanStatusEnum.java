package com.pay.enumUtils;

/**
 * Created by zwj on 2018/10/10.
 */

public enum CleanStatusEnum implements com.pay.dao.base.DatabaseEnum {
	/**
	 * 待下载（本地有了，但是还未从银行拉去数据）
	 */
	WAIT_DOWN {
		@Override
		public Integer getDBValue() {
			return 0;
		}
	},

	/**
	 * 待清算 （已经拉取了，但是还没有清算）
	 */
	WAIT_CLEAR {
		@Override
		public Integer getDBValue() {
			return 1;
		}
	},
	/**
	 * 已清算 （已经拉取了，但是还没有清算）
	 */
	HAVE_CLEAR {
		@Override
		public Integer getDBValue() {
			return 2;
		}
	},

	/**
	 * 已挂起 （已经拉取了，但是还没有清算）
	 */
	HUNG_UP {
		@Override
		public Integer getDBValue() {
			return 10;
		}
	},
	/**
	 * 清分到帐 （已经拉取了，但是还没有清算）
	 */
	PAY_CLEAN {
		@Override
		public Integer getDBValue() {
			return 4;
		}
	},
	/**
	 * 已挂起 （已经拉取了，但是还没有清算）
	 */
	ERROR {
		@Override
		public Integer getDBValue() {
			return 5;
		}
	},

	/**
	 * 清算中 （已经拉取了，但是还没有清算）
	 */
	CLEAR_ING {
		@Override
		public Integer getDBValue() {
			return 6;
		}
	},
	/**
	 * 清算中 （已经拉取了，但是还没有清算）
	 */

	PAY_ERROR {
		@Override
		public Integer getDBValue() {
			return 7;
		}
	};

	public static CleanStatusEnum getByDBValue(Integer dbValue) {
		if (WAIT_DOWN.getDBValue().equals(dbValue)) {
			return WAIT_DOWN;
		}
		if (WAIT_CLEAR.getDBValue().equals(dbValue)) {
			return WAIT_CLEAR;
		}
		if (HAVE_CLEAR.getDBValue().equals(dbValue)) {
			return HAVE_CLEAR;
		}
		if (ERROR.getDBValue().equals(dbValue)) {
			return ERROR;
		}
		if (CLEAR_ING.getDBValue().equals(dbValue)) {
			return CLEAR_ING;
		}
		if (PAY_ERROR.getDBValue().equals(dbValue)) {
			return PAY_ERROR;
		}
		if (PAY_CLEAN.getDBValue().equals(dbValue)) {
			return PAY_CLEAN;
		}
		if (HUNG_UP.getDBValue().equals(dbValue)) {
			return HUNG_UP;
		}

		return null;
	}

	public static CleanStatusEnum getByName(String cleanStatus) {
		if ("WAIT_DOWN".equals(cleanStatus)) {
			return WAIT_DOWN;
		}
		if ("WAIT_CLEAR".equals(cleanStatus)) {
			return WAIT_CLEAR;
		}
		if ("HAVE_CLEAR".equals(cleanStatus)) {
			return HAVE_CLEAR;
		}
		if ("ERROR".equals(cleanStatus)) {
			return ERROR;
		}
		if ("CLEAR_ING".equals(cleanStatus)) {
			return CLEAR_ING;
		}
		if ("PAY_ERROR".equals(cleanStatus)) {
			return PAY_ERROR;
		}
		if ("PAY_CLEAN".equals(cleanStatus)) {
			return PAY_CLEAN;
		}
		if ("HUNG_UP".equals(cleanStatus)) {
			return HUNG_UP;
		}
		return null;
	}
}
