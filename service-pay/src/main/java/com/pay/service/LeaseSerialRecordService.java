package com.pay.service;

import com.omv.database.bean.*;
import com.omv.database.biz.*;
import com.pay.entity.*;
import com.pay.enumUtils.*;

import java.util.*;

/**
 * Created by zwj&tdf on 2018/10/10.
 */
public interface LeaseSerialRecordService extends BaseService<LeaseSerialRecord, String> {
	/**
	 * 根据实例id 和原始订单号查找LeaseSerialRecord
	 * @param instanceId
	 * @param orderNo
	 * @return
	 */
	LeaseSerialRecord findByInstanceIdAndOrderNo(String instanceId, String orderNo);

	/**
	 * 根据流水号查找LeaseSerialRecord
	 * @param serialNo
	 * @return
	 */
	LeaseSerialRecord findBySerialNo(String serialNo);

	/**
	 * 根据实例id ，原始订单号，和订单日期查找 LeaseSerialRecord
	 * @param instanceId
	 * @param oriOrderNo
	 * @param orderDate
	 * @return
	 */
	LeaseSerialRecord findByInstanceIdAndOriOrderNoAndTransTime(String instanceId, String oriOrderNo, String orderDate);

	/**
	 * 批量修改 LeaseSerialRecord
	 * @param leaseSerialRecords
	 */
	void updateList(List<LeaseSerialRecord> leaseSerialRecords);

	/**
	 * 分页获取 LeaseSerialRecord
	 * @param date
	 * @param pageNo
	 * @param pageSize
	 * @param cleanStatusEnum
	 * @return
	 */
	PageModel list(String date, Integer pageNo, Integer pageSize, CleanStatusEnum cleanStatusEnum);

	/**
	 * 获取 	List<LeaseSerialRecord>
	 * @param dayBegin
	 * @param dayEnd
	 * @param dbValue
	 * @return
	 */
	List<LeaseSerialRecord> list(String dayBegin, String dayEnd, Integer dbValue);

	/**
	 * 根据原始订单好查询流水
	 * @param orderNo
	 * @return
	 */
	LeaseSerialRecord findByOriOrderNo(String orderNo);

	/**
	 * 根据订单好查询流水
	 * @param orderNo
	 * @return
	 */
	LeaseSerialRecord findByOrderNo(String orderNo);

	/**
	 * 根据实例id 和原始订单号获取List<LeaseSerialRecord>
	 * @param instanceId
	 * @param oriOrderNo
	 * @return
	 */
	List<LeaseSerialRecord> findByInstanceIdAndOriOrderNo(String instanceId, String oriOrderNo);

	/**
	 * 根据日期获取
	 * @param dayBegin
	 * @param dayEnd
	 * @return
	 */
	List<LeaseSerialRecord> findByDate(String dayBegin, String dayEnd);

	/**
	 * 分页获取交易流水表
	 * @param sql
	 * @param params
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	List<LeaseSerialRecord> findAllPage(String sql, List<Object> params, int pageNo, int pageSize);

	/**
	 * 计算总交易次数
	 * @param s
	 * @param list
	 * @return
	 */
	Object countAll(String s, List<Object> list);

	/**
	 * 根据清分状态查询 流水表
	 * @param cleanStatusEnum
	 * @return
	 */
	List<LeaseSerialRecord> select(CleanStatusEnum cleanStatusEnum);

	/**
	 * 修改状态
	 * @param date
	 * @param payError
	 */
	void updateByDate(String date, CleanStatusEnum payError);

	PageModel page(Map<String, Object> params, Integer pageNo, Integer pageSize);
}
