package com.pay.entity;


import com.omv.database.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_instance_expand_xyv2")
public class InstanceExpandXYV2 extends BaseEntity {
    //用于支付
    private String instanceId;
    private String mid;//商户号
    private String tid;//终端号
    private String msgSrc;//消息来源
    private String msgSrcId;//来源编号
    private String instMid;//机构商户号
    private String md5Key;//

    //用于报件
    private String reportAccesserId;
    private String reportPreKey;
    private String reportGroupNo;

    //用于租客认证
    private String authAppId;
    private String authAppKey;
    private String authSm2PubKey;
    private String authSm2PrvKey;

    private String channelType;//支付渠道 app-- app支付  csb--扫码支付 pub--公众号支付 report-进件  auth-认证

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMsgSrc() {
        return msgSrc;
    }

    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
    }

    public String getMsgSrcId() {
        return msgSrcId;
    }

    public void setMsgSrcId(String msgSrcId) {
        this.msgSrcId = msgSrcId;
    }

    public String getInstMid() {
        return instMid;
    }

    public void setInstMid(String instMid) {
        this.instMid = instMid;
    }

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }

    public String getReportAccesserId() {
        return reportAccesserId;
    }

    public void setReportAccesserId(String reportAccesserId) {
        this.reportAccesserId = reportAccesserId;
    }

    public String getReportPreKey() {
        return reportPreKey;
    }

    public void setReportPreKey(String reportPreKey) {
        this.reportPreKey = reportPreKey;
    }


    public String getReportGroupNo() {
        return reportGroupNo;
    }

    public void setReportGroupNo(String reportGroupNo) {
        this.reportGroupNo = reportGroupNo;
    }


    public String getAuthAppId() {
        return authAppId;
    }

    public void setAuthAppId(String authAppId) {
        this.authAppId = authAppId;
    }

    public String getAuthAppKey() {
        return authAppKey;
    }

    public void setAuthAppKey(String authAppKey) {
        this.authAppKey = authAppKey;
    }

    public String getAuthSm2PubKey() {
        return authSm2PubKey;
    }

    public void setAuthSm2PubKey(String authSm2PubKey) {
        this.authSm2PubKey = authSm2PubKey;
    }

    public String getAuthSm2PrvKey() {
        return authSm2PrvKey;
    }

    public void setAuthSm2PrvKey(String authSm2PrvKey) {
        this.authSm2PrvKey = authSm2PrvKey;
    }
}
