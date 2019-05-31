package com.sup.controller;

import com.sup.pojo.*;
import com.sup.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zwj on 2018/12/17.
 * 账单信息
 */
@RestController
@RequestMapping("/scrt/data/account")
@Api(value = "AccountingController", description = "账单信息", tags = {"账单信息"})
public class AccountingController extends BaseController<Accounting> {
	@Autowired
	public AccountingController(IAccountingMongleService iAccountingMongleService) {
		super.baseMongoDbService = iAccountingMongleService;
		super.aClass = Accounting.class;
	}
}
