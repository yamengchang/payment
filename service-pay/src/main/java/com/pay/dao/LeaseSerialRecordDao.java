package com.pay.dao;

import com.omv.database.repository.*;
import com.pay.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

/**
 * Created by zwj on 2018/10/10.
 */
public interface LeaseSerialRecordDao extends BaseRepository<LeaseSerialRecord, String> {
	LeaseSerialRecord findByInstanceIdAndOrderNo(String instanceId, String orderNo);

	LeaseSerialRecord findBySerialNo(String serialNo);

	@Query(value = "select ur.* from LeaseSerialRecord ur where ur.orderNo=:orderNo ", nativeQuery = true)
	LeaseSerialRecord getByOrderNo(@Param("orderNo") String orderNo);

	@Modifying
	@Query(value = "update tbl_lease_serial_record ur SET ur.clean_status=:cleanStatus WHERE ur.order_no=:orderNo", nativeQuery = true)
	void updateStatusByOrderNo(@Param("cleanStatus") Integer cleanStatus, @Param("orderNo") String orderNo);

	LeaseSerialRecord findByInstanceIdAndOriOrderNoAndTransTime(String instanceId, String oriOrderNo, String orderDate);

	LeaseSerialRecord findByOrderNo(String orderNo);

	List<LeaseSerialRecord> findByInstanceIdAndOriOrderNo(String instanceId, String oriOrderNo);

	@Query(value = "SELECT * FROM tbl_lease_serial_record WHERE create_time >=:dayBegin AND create_time <=:dayEnd AND clean_status=:dbValue AND bank_channel='xy'", nativeQuery = true)
	List<LeaseSerialRecord> list(@Param("dayBegin") String dayBegin, @Param("dayEnd") String dayEnd, @Param("dbValue") Integer dbValue);


	@Query(value = "SELECT * FROM tbl_lease_serial_record WHERE create_time >=:dayBegin AND create_time <=:dayEnd AND clean_status=:dbValue AND status=1 AND bank_channel='xy'", nativeQuery = true)
	List<LeaseSerialRecord> findByDate(@Param("dayBegin") String dayBegin, @Param("dayEnd") String dayEnd, @Param("dbValue") Integer dbValue);

	@Query(value = "SELECT * FROM tbl_lease_serial_record WHERE create_time >=:dayBegin AND create_time <=:dayEnd  AND status=1 AND bank_channel='xy' ", nativeQuery = true)
	List<LeaseSerialRecord> list(@Param("dayBegin") String dayBegin, @Param("dayEnd") String dayEnd);

	@Query(value = "SELECT * FROM tbl_lease_serial_record WHERE ori_order_no=:orderNo ", nativeQuery = true)
	LeaseSerialRecord findByOriOrderNo(@Param("orderNo") String orderNo);

	@Query(value = "SELECT * FROM tbl_lease_serial_record WHERE clean_status=:dbValue", nativeQuery = true)
	List<LeaseSerialRecord> findByCleanStatus(@Param("dbValue") Integer dbValue);
}
