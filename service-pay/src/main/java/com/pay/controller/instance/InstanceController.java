package com.pay.controller.instance;

import com.omv.common.util.basic.*;
import com.omv.common.util.signature.RsaUtils;
import com.omv.common.util.userutil.UserUtils;
import com.omv.database.bean.*;
import com.pay.bean.*;
import com.pay.entity.*;
import com.pay.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by zwj on 2018/9/30.
 */
@RestController
@Api(value = "InstanceController", description = "实例Controller")
public class InstanceController {
	@Autowired
	private InstanceService instanceService;

	@GetMapping("/create/key")
	@ApiOperation(value = "生成公钥及私钥", notes = "生成公钥及私钥")
	public String createPrivateKeyAndPublicKey() throws NoSuchAlgorithmException {
		return ValueUtil.toJson(RsaUtils.genKeyPair());
	}

	@PutMapping("/update/{instanceId}/key")
	@ApiOperation(value = "更新实例的公钥及私钥", notes = "更新实例的公钥及私钥")
	public String updateInstanceKey(@PathVariable("instanceId")String instanceId, Instance instance) {
		Instance dbInstance = instanceService.findOne(instanceId);
		dbInstance.setPrivateKey(instance.getPrivateKey());
		dbInstance.setPublicKey(instance.getPublicKey());
		instanceService.save(dbInstance);
		return ValueUtil.toJson();
	}


	/*
	 * 创建实例
	 * */
	@PostMapping("/instance")
	@ApiOperation(value = "创建实例", notes = "创建实例")
	public String create(Instance instance) throws Exception {
		ValueUtil.verifyParams("type,channel,companyName,privateKey,publicKey,merNo",
				instance.getType(),instance.getChannel(),instance.getCompanyName(),
				instance.getPrivateKey(),instance.getPublicKey(),instance.getMerNo());
		instance.setPlatformUserId(UserUtils.getUserId());
		instance.setInstanceId("I" + IDUtil.getID());
		instance.setInstanceKey(MD5.MD5Encode(IDUtil.getID()).toUpperCase());
		instanceService.saveInstance(instance);
		return ValueUtil.toJson();
	}

	/*
	 * 更新实例
	 * */
	@PutMapping("/instance")
	@ApiOperation(value =  "更新实例", notes = "更新实例")
	public String update(Instance instance) {
		instanceService.save(instance);
		return ValueUtil.toJson();
	}

	/*
	 * 实例详情(供给supervision)
	 * */
	@GetMapping("/instance/{id}")
	@ApiOperation(value =  "实例详情", notes = "实例详情")
	public String instanceInfo(@PathVariable("id") String instanceId) {
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.DATA.getCode());
		if(null == instance){
			ValueUtil.isError("无此实例");
		}
		return ValueUtil.toJson(instance);
	}

	/*
	 * 实例列表
	 * */
	@GetMapping("/instances")
	@ApiOperation(value =  "实例列表", notes = "实例列表")
	public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
		PageModel pageModel = PageModel.createPageMould(params, pageNo, pageSize);
		pageModel = instanceService.findAll(pageModel);
		return ValueUtil.toJson(pageModel);
	}

	/*
	 * 审核实例--通过
	 *
	 * */
	@PutMapping("/instance/{id}/audit/pass")
	@ApiOperation(value =  "审核实例--通过", notes = "审核实例--通过")
	public String auditPass(@PathVariable("id") String id) {
		Instance instance = instanceService.findOne(id);
		instance.setStatus(Constants.INSTANCE_USING);
		instanceService.save(instance);
		return ValueUtil.toJson();
	}

	/*
	 * 审核实例--驳回
	 *
	 * */
	@PutMapping("/instance/{id}/audit/reject")
	@ApiOperation(value =  "审核实例--驳回", notes = "审核实例--驳回")
	public String auditReject(@PathVariable("id") String id,String rejectReason) {
		ValueUtil.verifyParams("rejectReason",rejectReason);
		Instance instance = instanceService.findOne(id);
		instance.setStatus(Constants.INSTANCE_UNUSED);
		instanceService.save(instance);
		return ValueUtil.toJson();
	}

	/*
	 *完善支付信息
	 *
	 * */
	@PostMapping("/perfected/paymentInfo")
	@ApiOperation(value =  "完善支付信息", notes = "完善支付信息")
	public String perfectedPayment(Instance instance) throws IOException {
		instanceService.perfectedPayment(instance);
		return ValueUtil.toJson();
	}

}
