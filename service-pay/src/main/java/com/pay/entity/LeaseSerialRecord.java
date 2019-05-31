package com.pay.entity;

import com.omv.database.entity.*;
import com.pay.enumUtils.*;

import javax.persistence.*;

/*
 * 交易流水记录表 代收
 * */
@Entity
@Table(name = "tbl_lease_serial_record")
public class LeaseSerialRecord extends BaseEntity {
	@Column(columnDefinition = "varchar(32) COMMENT '支付平台订单号，平台唯一'")
	private String orderNo;
	@Column(columnDefinition = "varchar(32) COMMENT '第三方平台订单号，平台唯一'")
	private String oriOrderNo;
	@Column(columnDefinition = "varchar(32) COMMENT '实例id'")
	private String instanceId;
	@Column(columnDefinition = "varchar(50) COMMENT '流水的账号'")
	private String acctNo;
	@Column(columnDefinition = "varchar(50) COMMENT '流水号'")
	private String serialNo;
	@Column(columnDefinition = "varchar(20) COMMENT '交易时间 第三方平台订单的发起时间'")
	private String transTime;
	@Column(columnDefinition = "varchar(20) COMMENT '交易完成时间'")
	private String transOverTime;
	@Column(columnDefinition = "varchar(30) COMMENT '应收金额'")
	private String amount;
	@Column(columnDefinition = "varchar(32) COMMENT '用户编码'")
	private String userNo;
	@Column(columnDefinition = "varchar(32) COMMENT '用户id，平台唯一'")
	private String userId;
	@Column(columnDefinition = "varchar(2) COMMENT '交易状态  0-交易中  1-交易成功  2-交易失败 3-交易关闭'")
	private String status;
	@Column(columnDefinition = "varchar(2) COMMENT '退款状态  0-交易中  1-交易成功  2-交易失败'")
	private String refundStatus;
	@Column(columnDefinition = "varchar(11) COMMENT '退款金额'")
	private String refundAmt;
	@Column(columnDefinition = "varchar(2) COMMENT '流水类型  1-押金充值  2-押金退还  3-消费  4、便民缴费'")
	private String seriaType;
	@Column(columnDefinition = "varchar(11) COMMENT '实收金额'")
	private String realIncome;
	@Column(columnDefinition = "varchar(11) COMMENT '费率'")
	private String rate;
	@Column(columnDefinition = "varchar(11) COMMENT '服务费'")
	private String brokerage;
	@Column(columnDefinition = "varchar(10) COMMENT '支付渠道 微信app  微信扫码  支付宝JS等等'")
	private String paymentChannel;
	@Column(columnDefinition = "varchar(30) COMMENT '收款账号（卡号）'")
	private String receiveAcct;
	@Column(columnDefinition = "varchar(30) COMMENT '收款人姓名'")
	private String receiveAcctName;
	@Column(columnDefinition = "varchar(10) COMMENT '银行渠道 union-银联  xy-兴业'")
	private String bankChannel;
	@Column(columnDefinition = "varchar(1) COMMENT '是否对公账户 0-否  1-是'")
	private String isCompany;
	@Column(columnDefinition = "varchar(20) COMMENT '联行号/人行行号'")
	private String bankNo;
	@Column(columnDefinition = "varchar(20) COMMENT '交易机构商户号'")
	private String merId;
	@Column(columnDefinition = "varchar(20) COMMENT '交易终端商户号'")
	private String terMerId;


	//
	//	//银行的数据    都以b(bank)开头
	//					下单时间，平台订单号，第三方交易号，平台退款单号，第三方退款订单号,订单状态,交易方式，商品名称，订单金额，商家实收，手续费，费率
	@Column(columnDefinition = "varchar(50) COMMENT '下单时间'")
	private String bankOrderTime;
	@Column(columnDefinition = "varchar(50) COMMENT '平台订单号'")
	private String bankPlatformOrderNo;
	@Column(columnDefinition = "varchar(50) COMMENT '第三方交易号'")
	private String bankThirdPartyNo;
	@Column(columnDefinition = "varchar(50) COMMENT '商户退款单号'")
	private String bankRefundNo;
	@Column(columnDefinition = "varchar(50) COMMENT '平台退款单号'")
	private String bankPlatformRefundNo;
	@Column(columnDefinition = "varchar(50) COMMENT '第三方退款订单号'")
	private String bankThirdPartyRefundNo;
	@Column(columnDefinition = "varchar(50) COMMENT '订单状态'")
	private String bankOrderStatus;
	@Column(columnDefinition = "varchar(50) COMMENT '交易方式'")
	private String bankTradeMethod;
	@Column(columnDefinition = "varchar(50) COMMENT '商品名称'")
	private String bankGoodsName;
	@Column(columnDefinition = "varchar(50) COMMENT '订单金额'")
	private String bankOrderMoney;
	@Column(columnDefinition = "varchar(50) COMMENT '商家实收'")
	private String bankMerchantRealIncome;
	@Column(columnDefinition = "varchar(50) COMMENT '手续费'")
	private String bankServiceCharge;
	@Column(columnDefinition = "varchar(50) COMMENT '费率'")
	private String bankRate;


	@Column(columnDefinition = "int COMMENT '清算状态'")
	@Enumerated(EnumType.ORDINAL)
	private CleanStatusEnum cleanStatus = CleanStatusEnum.WAIT_DOWN;
	@Column(columnDefinition = "varchar(50) COMMENT '清分错误信息'")
	private String error;
	@Column(columnDefinition = "varchar(50) COMMENT '充账金额'")
	private String reconciliation;
	@Column(columnDefinition = "varchar(50) COMMENT '充账服务费'")
	private String reconciliationServiceCharge;


	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}


	public CleanStatusEnum getCleanStatus() {
		return cleanStatus;
	}

	public void setCleanStatus(CleanStatusEnum cleanStatus) {
		this.cleanStatus = cleanStatus;
	}

	public String getSeriaType() {
		return seriaType;
	}

	public void setSeriaType(String seriaType) {
		this.seriaType = seriaType;
	}


	public String getRealIncome() {
		return realIncome;
	}

	public void setRealIncome(String realIncome) {
		this.realIncome = realIncome;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(String brokerage) {
		this.brokerage = brokerage;
	}

	public String getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(String paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getBankOrderTime() {
		return bankOrderTime;
	}

	public void setBankOrderTime(String bankOrderTime) {
		this.bankOrderTime = bankOrderTime;
	}

	public String getBankPlatformOrderNo() {
		return bankPlatformOrderNo;
	}

	public void setBankPlatformOrderNo(String bankPlatformOrderNo) {
		this.bankPlatformOrderNo = bankPlatformOrderNo;
	}

	public String getBankThirdPartyNo() {
		return bankThirdPartyNo;
	}

	public void setBankThirdPartyNo(String bankThirdPartyNo) {
		this.bankThirdPartyNo = bankThirdPartyNo;
	}

	public String getBankPlatformRefundNo() {
		return bankPlatformRefundNo;
	}

	public void setBankPlatformRefundNo(String bankPlatformRefundNo) {
		this.bankPlatformRefundNo = bankPlatformRefundNo;
	}

	public String getBankThirdPartyRefundNo() {
		return bankThirdPartyRefundNo;
	}

	public void setBankThirdPartyRefundNo(String bankThirdPartyRefundNo) {
		this.bankThirdPartyRefundNo = bankThirdPartyRefundNo;
	}

	public String getBankOrderStatus() {
		return bankOrderStatus;
	}

	public void setBankOrderStatus(String bankOrderStatus) {
		this.bankOrderStatus = bankOrderStatus;
	}

	public String getBankTradeMethod() {
		return bankTradeMethod;
	}

	public void setBankTradeMethod(String bankTradeMethod) {
		this.bankTradeMethod = bankTradeMethod;
	}

	public String getBankGoodsName() {
		return bankGoodsName;
	}

	public void setBankGoodsName(String bankGoodsName) {
		this.bankGoodsName = bankGoodsName;
	}

	public String getBankOrderMoney() {
		return bankOrderMoney;
	}

	public void setBankOrderMoney(String bankOrderMoney) {
		this.bankOrderMoney = bankOrderMoney;
	}

	public String getBankMerchantRealIncome() {
		return bankMerchantRealIncome;
	}

	public void setBankMerchantRealIncome(String bankMerchantRealIncome) {
		this.bankMerchantRealIncome = bankMerchantRealIncome;
	}

	public String getBankServiceCharge() {
		return bankServiceCharge;
	}

	public void setBankServiceCharge(String bankServiceCharge) {
		this.bankServiceCharge = bankServiceCharge;
	}

	public String getBankRate() {
		return bankRate;
	}

	public void setBankRate(String bankRate) {
		this.bankRate = bankRate;
	}

	public String getReceiveAcct() {
		return receiveAcct;
	}

	public void setReceiveAcct(String receiveAcct) {
		this.receiveAcct = receiveAcct;
	}

	public String getOriOrderNo() {
		return oriOrderNo;
	}

	public void setOriOrderNo(String oriOrderNo) {
		this.oriOrderNo = oriOrderNo;
	}

	public String getReceiveAcctName() {
		return receiveAcctName;
	}

	public void setReceiveAcctName(String receiveAcctName) {
		this.receiveAcctName = receiveAcctName;
	}

	public String getTransOverTime() {
		return transOverTime;
	}

	public void setTransOverTime(String transOverTime) {
		this.transOverTime = transOverTime;
	}

	public String getBankRefundNo() {
		return bankRefundNo;
	}

	public void setBankRefundNo(String bankRefundNo) {
		this.bankRefundNo = bankRefundNo;
	}

	public String getReconciliation() {
		return reconciliation;
	}

	public void setReconciliation(String reconciliation) {
		this.reconciliation = reconciliation;
	}

	public String getReconciliationServiceCharge() {
		return reconciliationServiceCharge;
	}

	public void setReconciliationServiceCharge(String reconciliationServiceCharge) {
		this.reconciliationServiceCharge = reconciliationServiceCharge;
	}

	public String getBankChannel() {
		return bankChannel;
	}

	public void setBankChannel(String bankChannel) {
		this.bankChannel = bankChannel;
	}

	public String getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(String isCompany) {
		this.isCompany = isCompany;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getTerMerId() {
		return terMerId;
	}

	public void setTerMerId(String terMerId) {
		this.terMerId = terMerId;
	}
}
