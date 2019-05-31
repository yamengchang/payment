package com.pay.entity;

import com.omv.database.entity.BaseEntity;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zwj on 2018/10/15.
 * 实例对应的支付信息
 */
@Entity
@Table(name = "tbl_payment_info")
public class PaymentInfo extends BaseEntity {
    @Column(columnDefinition = "varchar(50) COMMENT '实例id'")
    private String instanceId;
    @Column(columnDefinition = "varchar(2) COMMENT '是否支持微信 0-激活中  1-已激活  2-激活失败'")
    private String wxIsEnabled;
    @Column(columnDefinition = "varchar(100) COMMENT '微信回调地址'")
    private String wxCallBackUrl;
    @Column(columnDefinition = "varchar(100) COMMENT '支付宝回调地址'")
    private String alipayCallBackUrl;
    @Column(columnDefinition = "varchar(2) COMMENT '是否支持支付宝  0-激活中  1-已激活  2-激活失败'")
    private String aliIsEnabled;
    @Column(columnDefinition = "varchar(20) COMMENT '银行卡号'")
    private String bankCardNo;
    @Column(columnDefinition = "varchar(100) COMMENT '银行卡支付回调地址'")
    private String bankCallBackUrl;
    @Column(columnDefinition = "varchar(2) COMMENT '是否支持银行卡  0-激活中  1-已激活  2-激活失败'")
    private String cardIsEnabled;
    @Column(columnDefinition = "varchar(100) COMMENT '页面通知地址'")
    private String returnUrl;
    @Column(columnDefinition = "varchar(100) COMMENT '退款回调地址'")
    private String refundUrl;
    //    @Column(columnDefinition = "varchar(50) COMMENT '商户Id'")
    //    private String mrchId;
    //    @Column(columnDefinition = "varchar(50) COMMENT '微信APP appid'")
    //    private String wxAppId;
    //    @Column(columnDefinition = "varchar(50) COMMENT '微信公众号  appid'")
    //    private String wapAppId;
    //    @Column(columnDefinition = "varchar(50) COMMENT '支付宝appid'")
    //    private String alipayAppId;
//    @Column(columnDefinition = "varchar(50) COMMENT '银联商户号'")
//    private String unionMerId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

//    public String getWxAppId() {
//        return wxAppId;
//    }
//
//    public void setWxAppId(String wxAppId) {
//        this.wxAppId = wxAppId;
//    }

    public String getWxCallBackUrl() {
        return wxCallBackUrl;
    }

    public void setWxCallBackUrl(String wxCallBackUrl) {
        this.wxCallBackUrl = wxCallBackUrl;
    }

//    public String getAlipayAppId() {
//        return alipayAppId;
//    }
//
//    public void setAlipayAppId(String alipayAppId) {
//        this.alipayAppId = alipayAppId;
//    }

    public String getAlipayCallBackUrl() {
        return alipayCallBackUrl;
    }

    public void setAlipayCallBackUrl(String alipayCallBackUrl) {
        this.alipayCallBackUrl = alipayCallBackUrl;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankCallBackUrl() {
        return bankCallBackUrl;
    }

    public void setBankCallBackUrl(String bankCallBackUrl) {
        this.bankCallBackUrl = bankCallBackUrl;
    }

//    public String getMrchId() {
//        return mrchId;
//    }
//
//    public void setMrchId(String mrchId) {
//        this.mrchId = mrchId;
//    }
//
//    public String getUnionMerId() {
//        return unionMerId;
//    }
//
//    public void setUnionMerId(String unionMerId) {
//        this.unionMerId = unionMerId;
//    }

    public String getWxIsEnabled() {
        return wxIsEnabled;
    }

    public void setWxIsEnabled(String wxIsEnabled) {
        this.wxIsEnabled = wxIsEnabled;
    }

    public String getAliIsEnabled() {
        return aliIsEnabled;
    }

    public void setAliIsEnabled(String aliIsEnabled) {
        this.aliIsEnabled = aliIsEnabled;
    }

    public String getCardIsEnabled() {
        return cardIsEnabled;
    }

    public void setCardIsEnabled(String cardIsEnabled) {
        this.cardIsEnabled = cardIsEnabled;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getRefundUrl() {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl) {
        this.refundUrl = refundUrl;
    }

//    public String getWapAppId() {
//        return wapAppId;
//    }
//
//    public void setWapAppId(String wapAppId) {
//        this.wapAppId = wapAppId;
//    }
}
