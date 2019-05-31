package com.sup.controller;

import com.alibaba.fastjson.*;
import com.omv.common.util.basic.*;
import com.omv.rabbit.service.*;
import com.sup.dto.*;
import com.sup.pojo.*;
import com.sup.service.*;
import io.jsonwebtoken.lang.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zwj on 2018/12/17.
 * 数据接收控制层
 */
@RestController
@RequestMapping("/scrt/data/lease")
@Api(value = "LeaseController", description = "合同信息", tags = {"合同信息"})
public class LeaseController extends BaseController<Lease> {
	@Autowired
	private IAccountingMongleService iAccountingMongleService;
	@Autowired
	private MQSenderService mqSenderService;

	@Autowired
	public LeaseController(ILeaseMongleService iLeaseMongleService) {
		super.baseMongoDbService = iLeaseMongleService;
		super.aClass = Lease.class;
	}

	@PostMapping("/save")
	@ApiOperation(value = "新增", notes = "新增")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	@SuppressWarnings("unchecked")
	public String receive(@RequestBody LeaseDto t) {
		Assert.notNull(t, "参数不能为空");
		Assert.notNull(t, "参数不能为空");
		return ValueUtil.toJson(mqSenderService.sendAndReceive(aClass.getSimpleName(), JSONObject.toJSONString(t)));
	}
}
