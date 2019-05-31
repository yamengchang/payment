package com.pay.entity;


import com.omv.database.entity.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tbl_instance_expand_union")
public class InstanceExpandUnion extends BaseEntity {
    private String instanceId;
    @Column(columnDefinition = "text COMMENT '收单 公钥'")
    private String acqPubKey;
    @Column(columnDefinition = "text COMMENT '收单 私钥'")
    private String acqPrvKey;
    @Column(columnDefinition = "text COMMENT '加签证书密码'")
    private String signCertPwd;
    @Column(columnDefinition = "text COMMENT '银联加签证书，base64转码'")
    private String signCert;
    @Column(columnDefinition = "text COMMENT '银联验签中级证书，base64转码'")
    private String middleCert;
    @Column(columnDefinition = "text COMMENT '银联验签跟证书，base64转码'")
    private String rootCert;
    @Column(columnDefinition = "varchar(50) COMMENT '银联商户号'")
    private String unionMerId;

    @Transient
    private MultipartFile acqPreFile;
    @Transient
    private MultipartFile acqPubFile;
    @Transient
    private MultipartFile middleCertFile;
    @Transient
    private MultipartFile rootCertFile;
    @Transient
    private MultipartFile signCertFile;

    public String getAcqPubKey() {
        return acqPubKey;
    }

    public void setAcqPubKey(String acqPubKey) {
        this.acqPubKey = acqPubKey;
    }

    public String getAcqPrvKey() {
        return acqPrvKey;
    }

    public void setAcqPrvKey(String acqPrvKey) {
        this.acqPrvKey = acqPrvKey;
    }

    public String getSignCertPwd() {
        return signCertPwd;
    }

    public void setSignCertPwd(String signCertPwd) {
        this.signCertPwd = signCertPwd;
    }

    public String getSignCert() {
        return signCert;
    }

    public void setSignCert(String signCert) {
        this.signCert = signCert;
    }

    public String getMiddleCert() {
        return middleCert;
    }

    public void setMiddleCert(String middleCert) {
        this.middleCert = middleCert;
    }

    public String getRootCert() {
        return rootCert;
    }

    public void setRootCert(String rootCert) {
        this.rootCert = rootCert;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public MultipartFile getAcqPreFile() {
        return acqPreFile;
    }

    public void setAcqPreFile(MultipartFile acqPreFile) {
        this.acqPreFile = acqPreFile;
    }

    public MultipartFile getAcqPubFile() {
        return acqPubFile;
    }

    public void setAcqPubFile(MultipartFile acqPubFile) {
        this.acqPubFile = acqPubFile;
    }

    public MultipartFile getMiddleCertFile() {
        return middleCertFile;
    }

    public void setMiddleCertFile(MultipartFile middleCertFile) {
        this.middleCertFile = middleCertFile;
    }

    public MultipartFile getRootCertFile() {
        return rootCertFile;
    }

    public void setRootCertFile(MultipartFile rootCertFile) {
        this.rootCertFile = rootCertFile;
    }

    public MultipartFile getSignCertFile() {
        return signCertFile;
    }

    public void setSignCertFile(MultipartFile signCertFile) {
        this.signCertFile = signCertFile;
    }

    public String getUnionMerId() {
        return unionMerId;
    }

    public void setUnionMerId(String unionMerId) {
        this.unionMerId = unionMerId;
    }
}
