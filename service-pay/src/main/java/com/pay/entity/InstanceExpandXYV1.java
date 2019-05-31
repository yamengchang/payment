package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_instance_expand_xyv1")
public class InstanceExpandXYV1 extends BaseEntity {
    private String instanceId;
    @Column(columnDefinition = "varchar(50) COMMENT '商户号'")
    private String appId;
    @Column(columnDefinition = "varchar(50) COMMENT '证书密码'")
    private String certPwd;
    @Column(columnDefinition = "varchar(50) COMMENT '商户密钥'")
    private String commKey;
    @Column(columnDefinition = "varchar(2000) COMMENT '兴业--平台加签私钥'")
    private String exchPrivateKey;
    @Column(columnDefinition = "varchar(2000) COMMENT '兴业--资管验签公钥'")
    private String ufmPublicKey;
    @Column(columnDefinition = "varchar(2000) COMMENT '兴业--密码加密公钥'")
    private String pwdPublicKey;
    @Column(columnDefinition = "varchar(64) COMMENT '兴业--开发者申请的KEYID'")
    private String keyId;
    @Column(columnDefinition = "varchar(64) COMMENT '兴业--开发者申请的KEYID'")
    private String mrchId;
    @Column(columnDefinition = "varchar(50) COMMENT '微信APP appid'")
    private String wxAppId;
    @Column(columnDefinition = "varchar(50) COMMENT '微信公众号  appid'")
    private String wapAppId;
    @Column(columnDefinition = "varchar(50) COMMENT '支付宝appid'")
    private String alipayAppId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCertPwd() {
        return certPwd;
    }

    public void setCertPwd(String certPwd) {
        this.certPwd = certPwd;
    }

    public String getCommKey() {
        return commKey;
    }

    public void setCommKey(String commKey) {
        this.commKey = commKey;
    }

    public String getExchPrivateKey() {
        return exchPrivateKey;
    }

    public void setExchPrivateKey(String exchPrivateKey) {
        this.exchPrivateKey = exchPrivateKey;
    }

    public String getUfmPublicKey() {
        return ufmPublicKey;
    }

    public void setUfmPublicKey(String ufmPublicKey) {
        this.ufmPublicKey = ufmPublicKey;
    }

    public String getPwdPublicKey() {
        return pwdPublicKey;
    }

    public void setPwdPublicKey(String pwdPublicKey) {
        this.pwdPublicKey = pwdPublicKey;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getMrchId() {
        return mrchId;
    }

    public void setMrchId(String mrchId) {
        this.mrchId = mrchId;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getWapAppId() {
        return wapAppId;
    }

    public void setWapAppId(String wapAppId) {
        this.wapAppId = wapAppId;
    }

    public String getAlipayAppId() {
        return alipayAppId;
    }

    public void setAlipayAppId(String alipayAppId) {
        this.alipayAppId = alipayAppId;
    }
}
