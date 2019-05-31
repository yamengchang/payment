package com.pay.dao.base;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.*;

import java.util.*;

public class BaseDaoImpl<T> implements BaseDao<T> {

	private static Logger logger = LoggerFactory.getLogger(BaseDaoImpl.class);

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Override
	public void update(String sql) {
		jdbcTemplate.update(sql);
	}

	@Override
	public void update(String sql, Object... params) {
		jdbcTemplate.update(sql, params);
	}

	@Override
	public void update(String sql, Map<String, Object> alias) {
		new NamedParameterJdbcTemplate(jdbcTemplate).update(sql, alias);
	}
}