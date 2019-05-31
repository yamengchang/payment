package com.pay.controller.life;

import com.omv.common.util.basic.*;
import com.pay.bean.*;
import com.pay.entity.*;
import com.pay.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by zwj on 2018/10/31.
 */
@Controller
public class ReportController {
	@Autowired
	private PayFactory payFactory;
	@Autowired
	private InstanceService instanceService;
	@Autowired
	private BusiAccountService busiAccountService;


	/*
	 * 上传图片--兴业报件使用
	 * */
	@PostMapping("/public/business/pic/upload")
	@ResponseBody
	public String picUpload(@RequestParam Map<String, Object> params, MultipartFile file) throws Exception {
		String instanceId = (String)params.get("instanceId");
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.CONSUME.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		params.put("instance", instance);
		PayService payService = payFactory.getInstance(Channel.getChannel(instance.getChannel()));
		params.put("file",file);
		return ValueUtil.toJson(payService.picUpload(params));
	}


	/*
	 * 商家报件
	 * */
	@PostMapping(value = "/scrt/business/report",produces = "application/json")
	@ResponseBody
	public String createBusinessReport(BusiAccount busiAccount) throws Exception {
		String instanceId = busiAccount.getInstanceId();
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.CONSUME.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		busiAccount.setInstance(instance);
		String channelType = instance.getChannel();
		Channel channel = Channel.getChannel(channelType);
		busiAccount.setAliProfCode("4900");//
		busiAccount.setWxProfCode("165");//事业单位公共缴费
		PayService payService = payFactory.getInstance(channel);

		return ValueUtil.toJson(payService.createReport(busiAccount));
	}

	/*
	 * 设置子商户号支付目录-Union /  兴业-小额验证
	 * */
	@PostMapping("/scrt/sub/busi/cata")
	@ResponseBody
	public String subBusiCata(BusiAccount busiAccount) throws Exception {
		String instanceId = busiAccount.getInstanceId();
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.CONSUME.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		busiAccount.setInstance(instance);
		String channelType = instance.getChannel();
		Channel channel = Channel.getChannel(channelType);
		PayService payService = payFactory.getInstance(channel);
		return ValueUtil.toJson(payService.subBusiCata(busiAccount));
	}

	/*
	 * 微信/支付宝报件
	 * */
	@PostMapping("/scrt/business/report/aw")
	@ResponseBody
	public String createBusinessReportAW(BusiAccount busiAccount, String reportChannel) throws Exception {
		ValueUtil.verifyParams("instanceId,number", busiAccount.getInstanceId(), busiAccount.getNumber());
		String instanceId = busiAccount.getInstanceId();
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.CONSUME.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		busiAccount.setInstance(instance);
		String channelType = instance.getChannel();
		PayService payService = payFactory.getInstance(Channel.getChannel(channelType));
		payService.createAWReport(busiAccount);
		return ValueUtil.toJson();
	}


	/*
	 * 商家报件信息修改
	 * */
	@PostMapping("/scrt/business/report/update")
	@ResponseBody
	public String updateBusinessReport(BusiAccount busiAccount) throws Exception {
		busiAccount.setAliProfCode("4900");//
		busiAccount.setWxProfCode("165");//事业单位公共缴费
		ValueUtil.verifyParams("instanceId,busiName,contactName,provinceId,cityId," + "cardNoCipher,isCompay,accBankNo,address,merNameAlias,servicePhone," + "contactEmail,contactPhone,cerdId,cardName,number", busiAccount);
		String instanceId = busiAccount.getInstanceId();
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.CONSUME.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		busiAccount.setInstance(instance);
		String channelType = instance.getChannel();
		PayService payService = payFactory.getInstance(Channel.getChannel(channelType));
		payService.updateReport(busiAccount);
		return ValueUtil.toJson();
	}


	/*
	 *@Author : Gavin
	 *@Email : gavinsjq@sina.com
	 *@Date: 2018/10/29 14:55
	 *@Description : 第三方查看终端报件、WX及Alipay报件状态
	 *@Params :  * @param null
	 */
	@PostMapping("/scrt/check/report/status")
	@ResponseBody
	@ApiOperation(value = "查看报件状态", notes = "查看报件状态")
	public String checkReportStatus(String instanceId, String number) throws Exception {
		Instance instance = instanceService.findByInstanceIdAndType(instanceId,InstanceTypeEnum.CONSUME.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		String channelType = instance.getChannel();
		PayService payService = payFactory.getInstance(Channel.getChannel(channelType));
		return ValueUtil.toJson(payService.checkReportStatus(instance, number));
	}

	/*
	 *@Author : Gavin
	 *@Email : gavinsjq@sina.com
	 *@Date: 2018/10/29 14:55
	 *@Description : 自平台查看终端报件、WX及Alipay报件状态
	 *@Params :  * @param null
	 */
	@PostMapping("/check/{id}/report/status")
	@ResponseBody
	@ApiOperation(value = "查看报件状态", notes = "查看报件状态")
	public String checkReportStatus(@PathVariable("id") String id) throws Exception {

		return ValueUtil.toJson();
	}


	/*
	 *@Author : tangdongfeng
	 *@Email : 1322008357@qq.com
	 *@Date: 2018/10/29 14:55
	 *@Description : 获取身份证证件信息并进⾏身份认证
	 *@Params :  * @param null
	 */
	@PostMapping("/scrt/ocr/idCardAuth")
	@ResponseBody
	@ApiOperation(value = "查看报件状态", notes = "查看报件状态")
	public String checkReportStatus(@RequestParam Map<String, Object> params) throws Exception {
		ValueUtil.verifyParams("instanceId,name,certNo,acctNo", params);

		Instance instance = instanceService.findByInstanceIdAndType((String)params.get("instanceId"), InstanceTypeEnum.AUTH.getCode());
		if (instance == null) {
			ValueUtil.isError("功能尚未开通");
		}
		Channel channel = Channel.getChannel(instance.getChannel());
		params.put("instance", instance);
		PayService payService = payFactory.getInstance(channel);
		return payService.ocrIdCardAuth(params);
	}
}
