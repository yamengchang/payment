package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by MING on 2019/3/6.
 * Description:
 */
@Entity
@Table(name = "tbl_convenient_payment")
public class ConvenientPayment extends BaseEntity {
    @Column(columnDefinition = "varchar(10) COMMENT '版本号'")
    private String version;
    @Column(columnDefinition = "varchar(10) COMMENT '字符集编码 可以使用UTF-8,GBK两种方式'")
    private String encoding;
    @Column(columnDefinition = "varchar(10) COMMENT '签名方法'")
    private String signMethod;
    @Column(columnDefinition = "varchar(10) COMMENT '交易类型'")
    private String txnType;
    @Column(columnDefinition = "varchar(10) COMMENT '交易子类型'")
    private String txnSubType;
    @Column(columnDefinition = "varchar(10) COMMENT '业务类型'")
    private String bizType;
    @Column(columnDefinition = "varchar(10) COMMENT '渠道类型'")
    private String channelType;
    @Column(columnDefinition = "varchar(10) COMMENT '接入类型'")
    private String accessType;
    @Column(columnDefinition = "varchar(10) COMMENT '交易币种'")
    private String currencyCode;
    @Column(columnDefinition = "varchar(30) COMMENT '商户号码'")
    private String merId;
    @Column(columnDefinition = "varchar(50) COMMENT '商户订单号'")
    private String orderId;
    @Column(columnDefinition = "varchar(50) COMMENT '实例ID'")
    private String instanceId;
    @Column(columnDefinition = "varchar(30) COMMENT '订单发送时间'")
    private String txnTime;
    @Column(columnDefinition = "varchar(10) COMMENT '金额（整数，精确到分）'")
    private String txnAmt;
    @Column(columnDefinition = "varchar(50) COMMENT '业务代码'")
    private String bussCode;
    @Column(columnDefinition = "varchar(30) COMMENT '原交易查询流水号'")
    private String origQryId;
    @Column(columnDefinition = "varchar(300) COMMENT '后台通知地址'")
    private String backUrl;
    @Column(columnDefinition = "varchar(500) COMMENT '账单要素'")
    private String billQueryInfo;
    @Column(columnDefinition = "varchar(10) COMMENT '渠道'")
    private String channel;
    @Column(columnDefinition = "varchar(30) COMMENT '户号'")
    private String usrNum;
    @Column(columnDefinition = "varchar(10) COMMENT '证件类型'")
    private String certIfTp;
    @Column(columnDefinition = "varchar(30) COMMENT '证件号码'")
    private String certIfId;
    @Column(columnDefinition = "varchar(30) COMMENT '姓名'")
    private String customerNm;
    @Column(columnDefinition = "varchar(50) COMMENT '加密证书ID'")
    private String encryptCertId;
    @Column(columnDefinition = "varchar(50) COMMENT '账号'")
    private String accNo;
    @Column(columnDefinition = "varchar(300) COMMENT '银行卡验证信息及身份信息'")
    private String customerInfo;
    @Column(columnDefinition = "varchar(300) COMMENT '接入结果'")
    private String accessMsg;
    @Column(columnDefinition = "varchar(300) COMMENT '回调结果'")
    private String callbackMsg;
    @Column(columnDefinition = "varchar(300) COMMENT '返回结果'")
    private String returnMsg;
    @Column(columnDefinition = "varchar(300) COMMENT '返回结果 0-成功 1-失败'")
    private String result;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnSubType() {
        return txnSubType;
    }

    public void setTxnSubType(String txnSubType) {
        this.txnSubType = txnSubType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getBussCode() {
        return bussCode;
    }

    public void setBussCode(String bussCode) {
        this.bussCode = bussCode;
    }

    public String getOrigQryId() {
        return origQryId;
    }

    public void setOrigQryId(String origQryId) {
        this.origQryId = origQryId;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getBillQueryInfo() {
        return billQueryInfo;
    }

    public void setBillQueryInfo(String billQueryInfo) {
        this.billQueryInfo = billQueryInfo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsrNum() {
        return usrNum;
    }

    public void setUsrNum(String usrNum) {
        this.usrNum = usrNum;
    }

    public String getCertIfTp() {
        return certIfTp;
    }

    public void setCertIfTp(String certIfTp) {
        this.certIfTp = certIfTp;
    }

    public String getCertIfId() {
        return certIfId;
    }

    public void setCertIfId(String certIfId) {
        this.certIfId = certIfId;
    }

    public String getCustomerNm() {
        return customerNm;
    }

    public void setCustomerNm(String customerNm) {
        this.customerNm = customerNm;
    }

    public String getEncryptCertId() {
        return encryptCertId;
    }

    public void setEncryptCertId(String encryptCertId) {
        this.encryptCertId = encryptCertId;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getAccessMsg() {
        return accessMsg;
    }

    public void setAccessMsg(String accessMsg) {
        this.accessMsg = accessMsg;
    }

    public String getCallbackMsg() {
        return callbackMsg;
    }

    public void setCallbackMsg(String callbackMsg) {
        this.callbackMsg = callbackMsg;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        if (this.result == null) {
            this.result = "1";
        }
    }
}
