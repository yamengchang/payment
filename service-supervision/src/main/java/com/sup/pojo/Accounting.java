package com.sup.pojo;

import io.swagger.annotations.*;

/**
 * Created by zwj on 2018/12/17.
 * accounting
 */
public class Accounting extends BaseEntity {
	private String instanceId;
	private String ownerLeaseId;
	private String thirdLeaseId;//第三方合同id
	private String thirdAcctId;//第三方合同id
	private String paymentsType;//收支类型  0-支出 1-收入
	private String startTime;//账单起始时间（yyyy-MM-dd）
	private String endTime; //账单截至时间（yyyy-MM-dd）
	private String acctType;//账单类型  00-水费   01-电费  02-燃气费  10-押金  11-租金  12-物业费  99-其他
	private String acctTypeNode; //账单类型备注
	private String receivable; //应收支金额
	private String received;//已收支金额
	private String receivableTime;//应收支日（yyyy-MM-dd）
	private String receivedTime;//实际收支日（yyyy-MM-dd HH:mm:ss）
	private String receivedChannel; //收支方式 cash-现金 wechat-微信 alipay-支付宝 bank-银行转账 pos-pos机 other-其他
	private String receivedChannelNode;//收支方式备注
	private String acctNode;//账单备注

	public String getOwnerLeaseId() {
		return ownerLeaseId;
	}

	public void setOwnerLeaseId(String ownerLeaseId) {
		this.ownerLeaseId = ownerLeaseId;
	}

	public String getThirdAcctId() {
		return thirdAcctId;
	}

	public void setThirdAcctId(String thirdAcctId) {
		this.thirdAcctId = thirdAcctId;
	}

	public String getThirdLeaseId() {
		return thirdLeaseId;
	}

	public void setThirdLeaseId(String thirdLeaseId) {
		this.thirdLeaseId = thirdLeaseId;
	}

	public String getPaymentsType() {
		return paymentsType;
	}

	public void setPaymentsType(String paymentsType) {
		this.paymentsType = paymentsType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getAcctTypeNode() {
		return acctTypeNode;
	}

	public void setAcctTypeNode(String acctTypeNode) {
		this.acctTypeNode = acctTypeNode;
	}

	public String getReceivable() {
		return receivable;
	}

	public void setReceivable(String receivable) {
		this.receivable = receivable;
	}

	public String getReceived() {
		return received;
	}

	public void setReceived(String received) {
		this.received = received;
	}

	public String getReceivableTime() {
		return receivableTime;
	}

	public void setReceivableTime(String receivableTime) {
		this.receivableTime = receivableTime;
	}

	public String getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(String receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getReceivedChannel() {
		return receivedChannel;
	}

	public void setReceivedChannel(String receivedChannel) {
		this.receivedChannel = receivedChannel;
	}

	public String getReceivedChannelNode() {
		return receivedChannelNode;
	}

	public void setReceivedChannelNode(String receivedChannelNode) {
		this.receivedChannelNode = receivedChannelNode;
	}

	public String getAcctNode() {
		return acctNode;
	}

	public void setAcctNode(String acctNode) {
		this.acctNode = acctNode;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}
