package com.pay.dao.impl;

import com.pay.dao.*;
import com.pay.dao.base.*;
import com.pay.entity.*;
import com.pay.enumUtils.*;
import org.springframework.stereotype.*;

import java.sql.*;
import java.util.*;

@Service
public class BillDaoImpl extends BaseDaoImpl<LeaseSerialRecord> implements IBillDao {


	@Override
	public Long updateList(List<LeaseSerialRecord> leaseSerialRecords, CleanStatusEnum cleanStatusEnum) {
		if (leaseSerialRecords == null || leaseSerialRecords.size() == 0) {
			return 0L;
		}
		String stringBuilder = "UPDATE tbl_lease_serial_record SET clean_status='" + cleanStatusEnum.getDBValue() + "',error=? WHERE id=?";
		Connection connection = null;
		PreparedStatement statement = null;
		Long money = 0L;
		try {
			connection = jdbcTemplate.getDataSource().getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(stringBuilder);
			int count = 0;
			for (LeaseSerialRecord t : leaseSerialRecords) {
				money += Long.parseLong(t.getBankOrderMoney() == null ?"0" : t.getBankOrderMoney());
				List<Object> values = getUpdateValue(t);
				for (int i = 0 ; i < values.size() ; i++) {
					statement.setObject(i + 1, values.get(i));
				}
				statement.addBatch();
				if (++count % 10 == 0) {
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
				}catch (SQLException e) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				}catch (SQLException ignored) {
				}
			}
		}
		return money;
	}

	@Override
	public void updateByOrder(String order, CleanStatusEnum payClean) {
		update("UPDATE tbl_lease_serial_record SET clean_status=? WHERE order_no=?", payClean, order);
	}


	private List<Object> getUpdateValue(LeaseSerialRecord t) {
		List<Object> objects = new ArrayList<>(2);
		objects.add(t.getError());
		objects.add(t.getId());
		return objects;
	}
}
