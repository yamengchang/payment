package com.sup.listener;

import com.alibaba.fastjson.*;
import com.omv.common.util.basic.*;
import com.omv.common.util.error.CustomException;
import com.omv.common.util.log.LoggerUtils;
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
public class LeaseDataDealWithListener {
	@Autowired
	private PayRemote payRemote;
	@Autowired
	private ILeaseMongleService iLeaseMongleService;
	@Autowired
	private IAccountingMongleService iAccountingMongleService;
	@Autowired
	private IHouseMongoService iHouseMongoService;
	private Class<Lease> aClass = Lease.class;
	private static final String name = "Lease";


	@RabbitListener(queues = name + "-save")
	public void save(String message) {
		Assert.isTrue(false, "WAIT WRITE");
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
		House houses = null;
		Lease lease = null;
		for (Object obj : dataList) {
			lease = (Lease) MapUtil.mapToObject( (Map) obj,Lease.class);
			param.put("instanceId", instanceId);
			param.put("thirdNo", lease.getThirdRoomId());
			lease.setInstanceId(instanceId);
			try {
				houses = iHouseMongoService.findOne(param);
				if (houses == null) {
					returnEntityList.add(ReturnEntity.error(null, lease.getThirdLeaseId(),"房源信息不存在"));
				}else {
					lease.setId(instanceId + "-" + lease.getThirdLeaseId());
					lease.setOwnerRoomId(houses.getId());
					Lease dbLease = (Lease) iLeaseMongleService.findById(lease.getId());
					if(null!=dbLease){
						lease.setLastUpdateTime(new Date());
						lease.setCreateTime(dbLease.getCreateTime());
						iLeaseMongleService.update(lease);
					}else{
						ValueUtil.verifyParams("thirdRoomId,roomName,thirdLeaseId,mortgageNum,payNum,rent," +
								"deposit,startTime,endTime,status,signingType,leaseType,tenantName,tenantPhone," +
								"tenantCertNo,tenantCertType,recPayee,recPhone",lease);
						if(lease.getLeaseType().equals("0")){
							ValueUtil.verifyParams("signId,signPart",lease);
						}
						iLeaseMongleService.save(lease);
					}
					returnEntityList.add(ReturnEntity.success(lease.getId(), lease.getThirdLeaseId()));
				}
			}catch (Exception e) {
				if(e instanceof CustomException){
					returnEntityList.add(ReturnEntity.error(null, lease.getThirdLeaseId(),e.getMessage()));
				}else{
					returnEntityList.add(ReturnEntity.error(null, lease.getThirdLeaseId(),"保存失败"));
				}
			}
		}
		LoggerUtils.info(receiveEntity.getTimestamp()+" 合同数据处理完成，开始回调");
		ThreadPoolUtils.addFixedThread(new HttpThread(notifyUrl, returnEntityList));
	}

	@RabbitListener(queues = name + "-update")
	public void update(String message) {
		Assert.notNull(message, "message 不能为空");
		iLeaseMongleService.save(JSONObject.parseObject(message, aClass));
	}
}
