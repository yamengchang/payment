package com.pay.entity;

import com.omv.database.entity.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by zwj on 2018/9/30.
 * 支付平台实例表
 */
@Entity
@Table(name = "tbl_instance")
public class Instance extends BaseEntity {
    @Column(columnDefinition = "varchar(50) COMMENT '实例id'")
    private String instanceId;
    @Column(columnDefinition = "varchar(50) COMMENT '实例密钥'")
    private String instanceKey;
    @Column(columnDefinition = "varchar(50) COMMENT '支付平台用户id'")
    private String platformUserId;
    @Column(columnDefinition = "varchar(50) COMMENT '公司名'")
    private String companyName;
    @Column(columnDefinition = "varchar(50) COMMENT '联系人'")
    private String contacts;
    @Column(columnDefinition = "varchar(50) COMMENT '联系人电话'")
    private String phone;
    @Column(columnDefinition = "varchar(50) COMMENT '联系人邮箱'")
    private String mail;
    @Column(columnDefinition = "varchar(2000) COMMENT '支付服务分发给个人的私钥'")
    private String privateKey;
    @Column(columnDefinition = "varchar(2000) COMMENT '支付服务分发给个人的公钥'")
    private String publicKey;
    @Column(columnDefinition = "varchar(2000) COMMENT '实例类型 0-押金 1-消费 2-便民缴费'")
    private String type;
    @Column(columnDefinition = "varchar(2000) COMMENT '实例对应渠道  XY-兴业  Union-银联'")
    private String channel;
    @Column(columnDefinition = "varchar(2) COMMENT '状态 0-申请中  1-申请成功  2-申请失败'")
    private String status;
    @Column(columnDefinition = "varchar(50) COMMENT '失败原因'")
    private String rejectReason;
    //    @Column(columnDefinition = "varchar(50) COMMENT '商户号'")
    //    private String appId;
    //    @Column(columnDefinition = "varchar(50) COMMENT '证书密码'")
    //    private String certPwd;
    //    @Column(columnDefinition = "varchar(50) COMMENT '商户密钥'")
    //    private String commKey;
//    @Column(columnDefinition = "varchar(2000) COMMENT '兴业--平台加签私钥'")
//    private String exchPrivateKey;
//    @Column(columnDefinition = "varchar(2000) COMMENT '兴业--资管验签公钥'")
//    private String ufmPublicKey;
//    @Column(columnDefinition = "varchar(2000) COMMENT '兴业--密码加密公钥'")
//    private String pwdPublicKey;
//    @Column(columnDefinition = "varchar(64) COMMENT '兴业--开发者申请的KEYID'")
//    private String keyId;
//    @Column(columnDefinition = "text COMMENT '收单 公钥'")
//    private String acqPubKey;
//    @Column(columnDefinition = "text COMMENT '收单 私钥'")
//    private String acqPrvKey;
//    @Column(columnDefinition = "text COMMENT '加签证书密码'")
//    private String signCertPwd;
//    @Column(columnDefinition = "text COMMENT '银联加签证书，base64转码'")
//    private String signCert;
//    @Column(columnDefinition = "text COMMENT '银联验签中级证书，base64转码'")
//    private String middleCert;
//    @Column(columnDefinition = "text COMMENT '银联验签跟证书，base64转码'")
//    private String rootCert;

    @Transient
    private String merNo;

    @Transient
    private InstanceExpandXYV1 expandXYV1;
    @Transient
    private InstanceExpandXYV2 expandXYV2;
    @Transient
    private InstanceExpandUnion expandUnion;
    @Transient
    private PaymentInfo paymentInfo;
    @Transient
    private List<PaymentChannel> paymentChannelList;
//    @Transient
//    private MultipartFile acqPreFile;
//    @Transient
//    private MultipartFile acqPubFile;
//    @Transient
//    private MultipartFile middleCertFile;
//    @Transient
//    private MultipartFile rootCertFile;
//    @Transient
//    private MultipartFile signCertFile;

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

//    public String getAppId() {
//        return appId;
//    }
//
//    public void setAppId(String appId) {
//        this.appId = appId;
//    }
//
//    public String getCertPwd() {
//        return certPwd;
//    }
//
//    public void setCertPwd(String certPwd) {
//        this.certPwd = certPwd;
//    }
//
//    public String getCommKey() {
//        return commKey;
//    }
//
//    public void setCommKey(String commKey) {
//        this.commKey = commKey;
//    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String getExchPrivateKey() {
//        return exchPrivateKey;
//    }
//
//    public void setExchPrivateKey(String exchPrivateKey) {
//        this.exchPrivateKey = exchPrivateKey;
//    }
//
//    public String getUfmPublicKey() {
//        return ufmPublicKey;
//    }
//
//    public void setUfmPublicKey(String ufmPublicKey) {
//        this.ufmPublicKey = ufmPublicKey;
//    }


//    public String getKeyId() {
//        return keyId;
//    }
//
//    public void setKeyId(String keyId) {
//        this.keyId = keyId;
//    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

//    public String getPwdPublicKey() {
//        return pwdPublicKey;
//    }
//
//    public void setPwdPublicKey(String pwdPublicKey) {
//        this.pwdPublicKey = pwdPublicKey;
//    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getMerNo() {
        return merNo;
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

//    public String getSignCertPwd() {
//        return signCertPwd;
//    }
//
//    public void setSignCertPwd(String signCertPwd) {
//        this.signCertPwd = signCertPwd;
//    }
//
//    public String getAcqPubKey() {
//        return acqPubKey;
//    }
//
//    public void setAcqPubKey(String acqPubKey) {
//        this.acqPubKey = acqPubKey;
//    }
//
//    public String getAcqPrvKey() {
//        return acqPrvKey;
//    }
//
//    public void setAcqPrvKey(String acqPrvKey) {
//        this.acqPrvKey = acqPrvKey;
//    }
//
//    public String getSignCert() {
//        return signCert;
//    }
//
//    public void setSignCert(String signCert) {
//        this.signCert = signCert;
//    }
//
//    public String getMiddleCert() {
//        return middleCert;
//    }
//
//    public void setMiddleCert(String middleCert) {
//        this.middleCert = middleCert;
//    }
//
//    public String getRootCert() {
//        return rootCert;
//    }
//
//    public void setRootCert(String rootCert) {
//        this.rootCert = rootCert;
//    }

//    public MultipartFile getAcqPreFile() {
//        return acqPreFile;
//    }
//
//    public void setAcqPreFile(MultipartFile acqPreFile) {
//        this.acqPreFile = acqPreFile;
//    }
//
//    public MultipartFile getAcqPubFile() {
//        return acqPubFile;
//    }
//
//    public void setAcqPubFile(MultipartFile acqPubFile) {
//        this.acqPubFile = acqPubFile;
//    }
//
//    public MultipartFile getMiddleCertFile() {
//        return middleCertFile;
//    }
//
//    public void setMiddleCertFile(MultipartFile middleCertFile) {
//        this.middleCertFile = middleCertFile;
//    }
//
//    public MultipartFile getRootCertFile() {
//        return rootCertFile;
//    }
//
//    public void setRootCertFile(MultipartFile rootCertFile) {
//        this.rootCertFile = rootCertFile;
//    }
//
//    public MultipartFile getSignCertFile() {
//        return signCertFile;
//    }
//
//    public void setSignCertFile(MultipartFile signCertFile) {
//        this.signCertFile = signCertFile;
//    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public List<PaymentChannel> getPaymentChannelList() {
        return paymentChannelList;
    }

    public void setPaymentChannelList(List<PaymentChannel> paymentChannelList) {
        this.paymentChannelList = paymentChannelList;
    }

    public InstanceExpandXYV2 getExpandXYV2() {
        return expandXYV2;
    }

    public void setExpandXYV2(InstanceExpandXYV2 expandXYV2) {
        this.expandXYV2 = expandXYV2;
    }

    public InstanceExpandUnion getExpandUnion() {
        return expandUnion;
    }

    public void setExpandUnion(InstanceExpandUnion expandUnion) {
        this.expandUnion = expandUnion;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public InstanceExpandXYV1 getExpandXYV1() {
        return expandXYV1;
    }

    public void setExpandXYV1(InstanceExpandXYV1 expandXYV1) {
        this.expandXYV1 = expandXYV1;
    }
}
