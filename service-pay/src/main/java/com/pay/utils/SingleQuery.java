//package com.pay.utils;
//
//import com.alibaba.fastjson.*;
//import com.cib.fintech.dfp.open.sdk.*;
//import com.cib.fintech.dfp.open.sdk.enums.*;
//import com.pay.bean.*;
//import org.slf4j.*;
//
//import java.util.*;
//
//public class SingleQuery {
//	private static final Logger logger = LoggerFactory.getLogger(ScheduledTime.class);
//
//	public static Boolean clearMoney(String orderNo, Integer times) {
//		Map<String, String> bodyParams = new HashMap<>(11);
//		bodyParams.put("order_no", orderNo);
//		bodyParams.put("mrch_id", Config.mrchId);
//		logger.info("开始执行远程调用");
//		String url = "/api/cloudpay/singlepayQuery";
//		String response = OpenSdk.gateway(url, ReqMethodEnum.POST, null, null, bodyParams);
//		JSONObject jsonObject = JSONObject.parseObject(response);
//		try {
//			logger.info("Thread.sleep(1000)...");
//			Thread.sleep(1000);
//		}catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println(jsonObject);
//		if ("SUCCESS".equals(jsonObject.get("status"))) {
//			return true;
//		}else {
//			try {
//				Thread.sleep(5000);
//			}catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			if (times == 3) {
//				return false;
//			}
//			return clearMoney(orderNo, times + 1);
//		}
//	}
//}
