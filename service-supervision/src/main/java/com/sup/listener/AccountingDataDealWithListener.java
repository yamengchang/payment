package com.sup.listener;

import com.alibaba.fastjson.*;
import com.omv.common.util.basic.*;
import com.omv.common.util.error.CustomException;
import com.omv.common.util.log.LoggerUtils;
import com.omv.common.util.signature.*;
import com.omv.common.util.treadPool.*;
import com.sup.pojo.*;
import com.sup.remote.*;
import com.sup.service.*;
import com.sup.thread.*;
import com.sup.util.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.util.*;

/**
 * Created by zwj on 2018/12/17.
 * 数据处理监听器
 */
@Component
public class AccountingDataDealWithListener {
	@Autowired
	private IAccountingMongleService iAccountingMongleService;
	@Autowired
	private PayRemote payRemote;
	@Autowired
	private ILeaseMongleService leaseMongleService;

	private Class<Accounting> aClass = Accounting.class;
	private static final String name = "Accounting";

	@RabbitListener(queues = name + "-save")
	public void save(String message) {
		Assert.notNull(message, "message 不能为空");
		iAccountingMongleService.save(JSONObject.parseObject(message, aClass));
	}

	@RabbitListener(queues = name + "-batchsave")
	public void batchsave(String message) {
		ReceiveEntity receiveEntity = JSONObject.parseObject(message, ReceiveEntity.class);
		String instanceId = receiveEntity.getInstanceId();
		String notifyUrl = receiveEntity.getNotifyUrl();
		String instanceInfo = payRemote.instanceInfo(instanceId);
		List dataList = DataDecodeUtil.getData(instanceInfo, receiveEntity);
		List<ReturnEntity> returnEntityList = new ArrayList<>(dataList.size());
		Map<String, String> param = new HashMap<>(2);
		for (Object obj : dataList) {
			Accounting accounting = (Accounting) MapUtil.mapToObject( (Map) obj,Accounting.class);
			accounting.setInstanceId(instanceId);
			param.put("thirdLeaseId", accounting.getThirdLeaseId());
			param.put("instanceId", instanceId);
			try {
				Lease lease = leaseMongleService.findOne(param);
				if (lease == null) {
					returnEntityList.add(ReturnEntity.error(null, accounting.getThirdLeaseId(),"合同信息不存在"));
				}else {
					accounting.setOwnerLeaseId(lease.getId());
					accounting.setId(instanceId + "-" + accounting.getThirdAcctId());

					Accounting dbAcct = (Accounting) iAccountingMongleService.findById(accounting.getId());
					if(null!=dbAcct){
						accounting.setLastUpdateTime(new Date());
						accounting.setCreateTime(dbAcct.getCreateTime());
						iAccountingMongleService.update(accounting);
					}else{
						ValueUtil.verifyParams("thirdLeaseId,thirdAcctId,paymentsType,acctType,receivable,receivableTime",accounting);
						iAccountingMongleService.save(accounting);
					}
					returnEntityList.add(ReturnEntity.success(accounting.getId(), accounting.getThirdLeaseId()));
				}
			}catch (Exception e) {
				if(e instanceof CustomException){
					returnEntityList.add(ReturnEntity.error(null, accounting.getThirdLeaseId(),e.getMessage()));
				}else{
					returnEntityList.add(ReturnEntity.error(null, accounting.getThirdLeaseId(),"保存失败"));
				}
			}

		}
		LoggerUtils.info(receiveEntity.getTimestamp()+" 账单数据处理完成，开始回调");
		ThreadPoolUtils.addFixedThread(new HttpThread(notifyUrl, returnEntityList));
	}

	private String getData(String instanceId, Map<String, Object> msg) {
		String instanceInfo = payRemote.instanceInfo(instanceId);
		String publicKey = ValueUtil.getFromJson(instanceInfo, "data", "content", "publicKey");
		String privateKey = ValueUtil.getFromJson(instanceInfo, "data", "content", "privateKey");
		EncryptManager encryptManager = new EncryptManager(privateKey, publicKey);
		String mobKey = (String)msg.get("mobKey");
		encryptManager.parseMobKey(mobKey);
		return encryptManager.decryptStr((String)msg.get("data"));
	}

	@RabbitListener(queues = name + "-update")
	public void update(String message) {
		Assert.notNull(message, "message 不能为空");
		iAccountingMongleService.save(JSONObject.parseObject(message, aClass));
	}
}
