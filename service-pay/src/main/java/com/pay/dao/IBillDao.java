package com.pay.dao;

import com.pay.dao.base.*;
import com.pay.entity.*;
import com.pay.enumUtils.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface IBillDao extends BaseDao<LeaseSerialRecord> {

//	List<LeaseSerialRecord> findByDate(String bill_date);

	/**
	 * 批量更新
	 * @param error
	 * @param cleanStatusEnum
	 * @return 交易金额总钱数 即代付总 额
	 */
	Long updateList(List<LeaseSerialRecord> error, CleanStatusEnum cleanStatusEnum);

	/**
	 * 根据order修改清分状态
	 * @param
	 * @param payClean
	 */
	void updateByOrder(String order, CleanStatusEnum payClean);

}
