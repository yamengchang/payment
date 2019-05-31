package com.pay.service.impl;

import com.omv.database.bean.PageModel;
import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.LeaseSerialRecordDao;
import com.pay.entity.LeaseSerialRecord;
import com.pay.enumUtils.CleanStatusEnum;
import com.pay.service.LeaseSerialRecordService;
import com.pay.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zwj on 2018/10/10.
 */
@Service
public class LeaseSerialRecordServiceImpl extends BaseServiceImpl<LeaseSerialRecord, String> implements LeaseSerialRecordService {
	private static final int BatchSize = 20;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private LeaseSerialRecordDao serialRecordDao;


	@Override
	public LeaseSerialRecord findByInstanceIdAndOrderNo(String instanceId, String orderNo) {
		return serialRecordDao.findByInstanceIdAndOrderNo(instanceId, orderNo);
	}

	@Override
	public LeaseSerialRecord findBySerialNo(String serialNo) {
		return serialRecordDao.findBySerialNo(serialNo);
	}

	@Override
	public LeaseSerialRecord findByInstanceIdAndOriOrderNoAndTransTime(String instanceId, String oriOrderNo, String orderDate) {
		return serialRecordDao.findByInstanceIdAndOriOrderNoAndTransTime(instanceId, oriOrderNo, orderDate);
	}


	@Override
	public void updateList(List<LeaseSerialRecord> leaseSerialRecords) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE tbl_lease_serial_record SET ");
		stringBuilder.append(" bank_goods_name=?,");
		stringBuilder.append(" bank_merchant_real_income=?,");
		stringBuilder.append(" bank_order_money=?,");
		stringBuilder.append(" bank_order_status=?,");
		stringBuilder.append(" bank_order_time=?,");
		stringBuilder.append(" bank_platform_order_no=?,");
		stringBuilder.append(" bank_platform_refund_no=?,");
		stringBuilder.append(" bank_rate=?,");
		stringBuilder.append(" bank_service_charge=?,");
		stringBuilder.append(" bank_third_party_no=?,");
		stringBuilder.append(" bank_third_party_refund_no=?,");
		stringBuilder.append(" bank_trade_method=?, ");
		stringBuilder.append(" clean_status=? ");
		stringBuilder.append(" WHERE order_no=?");
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = jdbcTemplate.getDataSource().getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(stringBuilder.toString());
			int count = 0;
			for (LeaseSerialRecord t : leaseSerialRecords) {
				List<Object> values = getInsertParam(t);
				for (int i = 0 ; i < values.size() ; i++) {
					statement.setObject(i + 1, values.get(i));
				}
				statement.addBatch();
				if (++count % BatchSize == 0) {
					statement.executeBatch();
					connection.commit();
				}
			}
			statement.executeBatch();
			connection.commit();
		}catch (SQLException ignored) {
		}finally {
			if (statement != null) {
				try {
					statement.close();
				}catch (SQLException ignored) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				}catch (SQLException ignored) {
				}
			}
		}
	}

	@Override
	public PageModel list(String date, Integer pageNo, Integer pageSize, CleanStatusEnum cleanStatusEnum) {
		//差错账，和挂起账
		if (cleanStatusEnum == CleanStatusEnum.WAIT_CLEAR) {
			StringBuilder stringBuilder = new StringBuilder(" FROM tbl_lease_serial_record WHERE   create_time<='" + DateUtil.getDayEndString(date) + "'");
			stringBuilder.append(" AND status='1' AND clean_status=? AND( IFNULL(amount,0)!=IFNULL(bank_order_money,0) OR IFNULL(real_income,0)!=IFNULL(bank_merchant_real_income,0) OR IFNULL(brokerage,0)!=IFNULL(bank_service_charge,0) )");
			Object[] value = new Object[] {cleanStatusEnum.getDBValue()};
			return templete(stringBuilder, value, pageNo, pageSize);
		}else if (cleanStatusEnum == CleanStatusEnum.HUNG_UP) {
			// 挂账
			StringBuilder stringBuilder = new StringBuilder(" FROM tbl_lease_serial_record WHERE status='1' AND bank_channel='xy' AND create_time>='" + DateUtil.getDayStartString(date) + "' AND create_time<='" + DateUtil.getDayEndString(date) + "'");
			stringBuilder.append(" AND clean_status=? ");
			Object[] value = new Object[] {CleanStatusEnum.HUNG_UP.getDBValue()};
			return templete(stringBuilder, value, pageNo, pageSize);
		}else {
			StringBuilder stringBuilder = new StringBuilder(" FROM tbl_lease_serial_record WHERE status='1' AND bank_channel='xy' AND create_time>='" + DateUtil.getDayStartString(date) + "' AND create_time<='" + DateUtil.getDayEndString(date) + "' ");
			stringBuilder.append(" AND (clean_status=? or clean_status=? ) ");
			Object[] value = new Object[] {CleanStatusEnum.PAY_ERROR.getDBValue(), CleanStatusEnum.ERROR.getDBValue()};
			return templete(stringBuilder, value, pageNo, pageSize);
		}

	}

	/**
	 * 差错账
	 * @param stringBuilder
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private PageModel templete(StringBuilder stringBuilder, Object[] value, Integer pageNo, Integer pageSize) {

		Long count = jdbcTemplate.query("SELECT COUNT(1) c " + stringBuilder.toString(), value, new ResultSetExtractor<Long>() {
			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (!rs.next()) {
					return 0L;
				}
				return (long)rs.getInt("c");
			}
		});
		Object para[] = new Object[value.length + 2];
		System.arraycopy(value, 0, para, 0, value.length);
		para[para.length - 2] = pageNo - 1;
		para[para.length - 1] = pageSize;
		List<Map<java.lang.String, Object>> list = jdbcTemplate.queryForList("SELECT * " + stringBuilder.toString() + "  ORDER BY create_time DESC limit ?,?", para);

		PageModel pageModel = new PageModel();
		pageModel.setContent(list);
		pageModel.setPageSize(pageSize);
		pageModel.setPage(pageNo);
		pageModel.setTotalRows(count);
		return pageModel;
	}

	@Override
	public List<LeaseSerialRecord> list(String dayBegin, String dayEnd, Integer dbValue) {
		return serialRecordDao.list(dayBegin, dayEnd, dbValue);
	}

	@Override
	public List<LeaseSerialRecord> findByDate(String dayBegin, String dayEnd) {
		return serialRecordDao.list(dayBegin, dayEnd);
	}

	@Override
	public List<LeaseSerialRecord> findAllPage(String sql, List<Object> params, int paeNo, int pageSize) {
		return findBySql(sql, params, paeNo, pageSize);
	}

	@Override
	public Long countAll(String s, List<Object> list) {
		return (Long)findBySqlCount(s, list);
	}

	@Override
	public List<LeaseSerialRecord> select(CleanStatusEnum cleanStatusEnum) {
		try {
			return serialRecordDao.findByCleanStatus(cleanStatusEnum.getDBValue());
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateByDate(String date, CleanStatusEnum payError) {
		jdbcTemplate.update("UPDATE tbl_lease_serial_record SET clean_status=? WHERE  create_time>=? AND  create_time<=?", payError.getDBValue(), DateUtil.getDayStartString(date), DateUtil.getDayEndString(date));
	}

	@Override
	public PageModel page(Map<String, Object> params, Integer pageNo, Integer pageSize) {
		return null;
	}


	private PageModel template(String date, Integer pageNo, Integer pageSize, CleanStatusEnum cleanStatusEnum) {

		//差错账，和挂起账
		StringBuilder stringBuilder = new StringBuilder(" FROM tbl_lease_serial_record WHERE   create_time<='" + date + " 23:59:59'");
		stringBuilder.append(" AND status='1' AND clean_status=? AND( IFNULL(amount,0)!=IFNULL(bank_order_money,0) OR IFNULL(real_income,0)!=IFNULL(bank_merchant_real_income,0) OR IFNULL(brokerage,0)!=IFNULL(bank_service_charge,0) )");

		Object[] value = new Object[] {cleanStatusEnum.getDBValue()};
		List<Map<java.lang.String, Object>> list = jdbcTemplate.queryForList("SELECT * " + stringBuilder.toString() + "  ORDER BY create_time DESC limit ?,?", cleanStatusEnum.getDBValue(), pageNo - 1, pageSize);
		Long count = jdbcTemplate.query("SELECT COUNT(1) c " + stringBuilder.toString(), value, new ResultSetExtractor<Long>() {
			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (!rs.next()) {
					return 0L;
				}

				return (long)rs.getInt("c");
			}
		});
		PageModel pageModel = new PageModel();
		pageModel.setContent(list);
		pageModel.setPageSize(pageSize);
		pageModel.setPage(pageNo);
		pageModel.setTotalRows(count);
		return pageModel;
	}

	private List<Object> getInsertParam(LeaseSerialRecord t) {
		List<Object> object = new ArrayList<>(13);
		object.add(t.getBankGoodsName());
		object.add(t.getBankMerchantRealIncome());
		object.add(t.getBankOrderMoney());
		object.add(t.getBankOrderStatus());
		object.add(t.getBankOrderTime());
		object.add(t.getBankPlatformOrderNo());
		object.add(t.getBankPlatformRefundNo());
		object.add(t.getBankRate());
		object.add(t.getBankServiceCharge());
		object.add(t.getBankThirdPartyNo());
		object.add(t.getBankThirdPartyRefundNo());
		object.add(t.getBankTradeMethod());
		object.add(t.getCleanStatus().getDBValue());
		object.add(t.getOrderNo());
		return object;
	}

	@Override
	public LeaseSerialRecord findByOriOrderNo(String orderNo) {
		return serialRecordDao.findByOriOrderNo(orderNo);
	}

	@Override
	public LeaseSerialRecord findByOrderNo(String orderNo) {
		return serialRecordDao.findByOrderNo(orderNo);
	}

	@Override
	public List<LeaseSerialRecord> findByInstanceIdAndOriOrderNo(String instanceId, String oriOrderNo) {
		return serialRecordDao.findByInstanceIdAndOriOrderNo(instanceId, oriOrderNo);
	}
}
