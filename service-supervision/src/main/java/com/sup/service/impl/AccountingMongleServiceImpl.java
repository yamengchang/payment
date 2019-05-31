package com.sup.service.impl;

import com.sup.pojo.*;
import com.sup.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

/**
 * @author tdf
 * @date 2018-12-18 10:13
 **/
@Service
public class AccountingMongleServiceImpl extends BaseMongoDbServiceImpl<Accounting> implements IAccountingMongleService {
	@Autowired
	public AccountingMongleServiceImpl() {
		super(Accounting.class);
	}
}
