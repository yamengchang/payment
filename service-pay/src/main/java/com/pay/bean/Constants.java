package com.pay.bean;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Created by zwj on 2018/9/28.
 */
@Component
public class Constants {
	public static Map<String,String> paymentChannel = new HashedMap();
	static {
		paymentChannel.put("wechat_app","微信APP");
		paymentChannel.put("wechat_csb","微信扫码支付");
		paymentChannel.put("wechat_wap","微信公众号支付");
		paymentChannel.put("alipay_app","支付宝APP");
		paymentChannel.put("alipay_csb","支付宝扫码支付");
		paymentChannel.put("bank_app","银联云闪付APP支付");
		paymentChannel.put("bank_wap","银联云闪付WAP支付");
		paymentChannel.put("bank_apl","Apple Pay支付");
	}

	@Value("${payment.url:http://119.28.135.68/payment}")
	public String PAYMENT_DOMAIN;



	@Value("${bank.KeyId:}")
	private String KeyId;
	@Value("${bank.PriKey:}")
	private String PriKey;
	@Value("${bank.DevEnv:true}")
	private Boolean DevEnv;
	@Value("${bank.DevUrl:}")
	private String DevUrl;
	@Value("${mrch_id:}")
	private String mrchId;

	@Value("${remotePullDataUrl:}")
	private String remotePullDataUrl;
	@Value("${remoteUserUrl:}")
	private String remoteUserUrl;

	@Value("${union.trans_url:}")
	private String unionTransUrl;

	@Value("${house.verify.enable:false}")
	private Boolean houseVerifyEnable;

	public String getMrchId() {
		return mrchId;
	}

	public void setMrchId(String mrchId) {
		this.mrchId = mrchId;
	}

	public static final String INSTANCE_USING = "1";//实例状态  可用
	public static final String INSTANCE_UNUSED = "0";//实例状态  不可用

	public static final String BIND_CARD_SUCCESS = "3";//账号状态  绑卡成功

	public static final String OPEN_ACCOUNT_SUCCESS = "1";//账号状态  开户成功

	public static final String SERIAL_TYPE_RECHARGE = "1";//流水类型  充值

	public static final String SERIAL_TYPE_RENT = "3";//流水类型  租金

	public static final String RECHARGE_STATUS_SUCCESS = "1";//充值状态  成功

	public static final String RECHARGE_STATUS_FAILED = "2";//充值状态  失败

	public static final String PAYMENT_STATUS_WAITING = "4";//交易状态  待支付

	public static final String PAYMENT_STATUS_TRADING = "0";//交易状态  交易中

	public static final String PAYMENT_STATUS_SUCCESS = "1";//交易状态  交易成功

	public static final String PAYMENT_STATUS_FAILED = "2";//交易状态  交易失败

	public static final String PAYMENT_STATUS_CLOSE = "3";//交易状态  交易失败

	public static final String CREATE_AGREEMENT_FAILED = "3"; //建立协议失败

	public static final String CHANGE_AGREEMENT_FAILED = "4";//变更协议失败




	public String getPAYMENT_DOMAIN() {
		return PAYMENT_DOMAIN;
	}

	public void setPAYMENT_DOMAIN(String PAYMENT_DOMAIN) {
		this.PAYMENT_DOMAIN = PAYMENT_DOMAIN;
	}


	public static Map<String, Map<String, String>> getPaymentRateInfo() {
		Map<String, Map<String, String>> rate = new HashMap<>();
		Map<String, String> wechat_app = new HashMap<>();
		wechat_app.put("rate", "0.2");
		wechat_app.put("brokerage", "0.4");
		rate.put("wechat_app", wechat_app);

		Map<String, String> wechat_csb = new HashMap<>();
		wechat_app.put("rate", "0.2");
		wechat_app.put("brokerage", "0.4");
		rate.put("wechat_csb", wechat_csb);

		Map<String, String> alipay_csb = new HashMap<>();
		wechat_app.put("rate", "0.2");
		wechat_app.put("brokerage", "0.4");
		rate.put("wechat_wap", alipay_csb);

		Map<String, String> wechat_wap = new HashMap<>();
		wechat_app.put("rate", "0.2");
		wechat_app.put("brokerage", "0.4");
		rate.put("alipay_csb", wechat_wap);
		return rate;
	}

	public String getKeyId() {
		return KeyId;
	}

	public void setKeyId(String keyId) {
		KeyId = keyId;
	}

	public String getPriKey() {
		return PriKey;
	}

	public void setPriKey(String priKey) {
		PriKey = priKey;
	}

	public Boolean getDevEnv() {
		return DevEnv;
	}

	public void setDevEnv(Boolean devEnv) {
		DevEnv = devEnv;
	}

	public String getDevUrl() {
		return DevUrl;
	}

	public void setDevUrl(String devUrl) {
		DevUrl = devUrl;
	}

	public String getRemotePullDataUrl() {
		return remotePullDataUrl;
	}

	public void setRemotePullDataUrl(String remotePullDataUrl) {
		this.remotePullDataUrl = remotePullDataUrl;
	}

	public String getRemoteUserUrl() {
		return remoteUserUrl;
	}

	public void setRemoteUserUrl(String remoteUserUrl) {
		this.remoteUserUrl = remoteUserUrl;
	}

	public String getUnionTransUrl() {
		return unionTransUrl;
	}

	public void setUnionTransUrl(String unionTransUrl) {
		this.unionTransUrl = unionTransUrl;
	}

	public Boolean getHouseVerifyEnable() {
		return houseVerifyEnable;
	}

	public void setHouseVerifyEnable(Boolean houseVerifyEnable) {
		this.houseVerifyEnable = houseVerifyEnable;
	}
}
