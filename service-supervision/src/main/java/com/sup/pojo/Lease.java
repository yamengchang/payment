package com.sup.pojo;

/**
 * Created by zwj on 2018/12/17.
 * 合同信息
 */
public class Lease extends BaseEntity {
	private String thirdLeaseId;
	private String instanceId;
	private String ownerRoomId;
	private String thirdRoomId;//第三方房间id号
	private String roomName;//房间名称（万达广场A坐-501）
	private String leaseNo; //租约编号
	private String mortgageNum;//押（押1）
	private String payNum;//付（付3）
	private String rent;//租金
	private String deposit;// 押金
	private String startTime; // 租约起始时间（yyyy-MM-dd）
	private String endTime;// 租约截至时间（yyyy-MM-dd）
	private String status; // 租约状态 0-合同待确认  1-合同待签  2-合同已签
	private String signingTime;//签约时间（yyyy-MM-dd）
	private String signingType; // 签约类型 0-新签  1-续签
	private String leaseType;//租约/合同类型  0-电子合同   1-纸质合同
	private String tenantName;//承租人姓名
	private String tenantPhone;//承租人电话
	private String tenantCertNo;//证件编号
	private String tenantCertType;//证件类型

	private String recPayee;//收款人姓名
	private String recPhone; //收款人电话

	// 签约方(如上上签)
	private String signPart;
	//签约方id（上上签的合同id号）
	private String signId;
	//合同的真实性
	private String effective;

	public String getThirdLeaseId() {
		return thirdLeaseId;
	}

	public void setThirdLeaseId(String thirdLeaseId) {
		this.thirdLeaseId = thirdLeaseId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getOwnerRoomId() {
		return ownerRoomId;
	}

	public void setOwnerRoomId(String ownerRoomId) {
		this.ownerRoomId = ownerRoomId;
	}

	public String getThirdRoomId() {
		return thirdRoomId;
	}

	public void setThirdRoomId(String thirdRoomId) {
		this.thirdRoomId = thirdRoomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getLeaseNo() {
		return leaseNo;
	}

	public void setLeaseNo(String leaseNo) {
		this.leaseNo = leaseNo;
	}

	public String getMortgageNum() {
		return mortgageNum;
	}

	public void setMortgageNum(String mortgageNum) {
		this.mortgageNum = mortgageNum;
	}

	public String getPayNum() {
		return payNum;
	}

	public void setPayNum(String payNum) {
		this.payNum = payNum;
	}

	public String getRent() {
		return rent;
	}

	public void setRent(String rent) {
		this.rent = rent;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSigningTime() {
		return signingTime;
	}

	public void setSigningTime(String signingTime) {
		this.signingTime = signingTime;
	}

	public String getSigningType() {
		return signingType;
	}

	public void setSigningType(String signingType) {
		this.signingType = signingType;
	}

	public String getLeaseType() {
		return leaseType;
	}

	public void setLeaseType(String leaseType) {
		this.leaseType = leaseType;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantPhone() {
		return tenantPhone;
	}

	public void setTenantPhone(String tenantPhone) {
		this.tenantPhone = tenantPhone;
	}


	public void setTenantCertType(String tenantCertType) {
		this.tenantCertType = tenantCertType;
	}

	public String getTenantCertNo() {
		return tenantCertNo;
	}

	public void setTenantCertNo(String tenantCertNo) {
		this.tenantCertNo = tenantCertNo;
	}

	public String getTenantCertType() {
		return tenantCertType;
	}

	public String getRecPayee() {
		return recPayee;
	}

	public void setRecPayee(String recPayee) {
		this.recPayee = recPayee;
	}

	public String getRecPhone() {
		return recPhone;
	}

	public void setRecPhone(String recPhone) {
		this.recPhone = recPhone;
	}

	public String getSignPart() {
		return signPart;
	}

	public void setSignPart(String signPart) {
		this.signPart = signPart;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}
}
