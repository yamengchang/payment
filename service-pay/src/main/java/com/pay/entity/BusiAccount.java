package com.pay.entity;

import com.omv.database.entity.BaseEntity;
import com.pay.vo.Pic;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by zwj on 2018/10/29.
 * 商家信息表(用于银联统一支付)
 */
@Entity
@Table(name = "tbl_busi_account")
public class BusiAccount extends BaseEntity{
    @Column(columnDefinition = "varchar(32) COMMENT '实例id，平台唯一'")
    private String instanceId;
    @Column(columnDefinition = "varchar(32) COMMENT '第三方机构商户号，平台唯一'")
    private String merNo;
    @Column(columnDefinition = "varchar(32) COMMENT '商家终端商户号'")
    private String terMerNo;
    @Column(columnDefinition = "varchar(64) COMMENT '店铺名称'")
    private String busiName;
    @Column(columnDefinition = "varchar(30) COMMENT '联系人'")
    private String contactName;
    @Column(columnDefinition = "varchar(11) COMMENT '终端商户所在省份'")
    private String provinceId;
    @Column(columnDefinition = "varchar(11) COMMENT '终端商户所在城市'")
    private String cityId;
    @Column(columnDefinition = "varchar(100) COMMENT '地址'")
    private String address;
    @Column(columnDefinition = "varchar(20) COMMENT '商户简称'")
    private String merNameAlias;
    @Column(columnDefinition = "varchar(64) COMMENT '客服电话'")
    private String servicePhone;
    @Column(columnDefinition = "varchar(64) COMMENT '备注'")
    private String note;
    @Column(columnDefinition = "varchar(64) COMMENT '联系人邮箱'")
    private String contactEmail;
    @Column(columnDefinition = "varchar(64) COMMENT '联系电话'")
    private String contactPhone;
    @Column(columnDefinition = "varchar(64) COMMENT '身份证号'")
    private String cerdId;
    @Column(columnDefinition = "varchar(64) COMMENT '结算账号  即银行卡号'")
    private String cardNoCipher;
    @Column(columnDefinition = "varchar(30) COMMENT '结算账号户名'")
    private String cardName;
    @Column(columnDefinition = "varchar(2) COMMENT '是否对公账户  0：否1：是'")
    private String isCompay;
    @Column(columnDefinition = "varchar(8) COMMENT '第三方平台商户唯一识别号，必须是8位的数字(8位数字仅限于银联)'")
    private String number;
    @Column(columnDefinition = "varchar(20) COMMENT '微信报件子商户号'")
    private String wxBankSpCode;
    @Column(columnDefinition = "varchar(20) COMMENT '支付宝报件子商户号'")
    private String aliBankSpCode;
    @Column(columnDefinition = "varchar(20) COMMENT '联行号'")
    private String accBankNo;
    @Column(columnDefinition = "varchar(32) COMMENT '原交易流水号'")
    private String oriRequestNo;
    @Column(columnDefinition = "varchar(2) COMMENT '终端报件状态 0-审核中  1-审核通过 2-审核驳回 3-开户成功'")
    private String terReportStatus;
    @Column(columnDefinition = "varchar(2) COMMENT '支付宝报件状态 0-审核中  1-审核通过 2-审核驳回'")
    private String aliEnabledStatus;
    @Column(columnDefinition = "varchar(2) COMMENT '微信报件状态 0-审核中  1-审核通过 2-审核驳回'")
    private String wxEnabledStatus;
    @Column(columnDefinition = "varchar(10) COMMENT '支付宝行业类别编码'")
    private String aliProfCode;
    @Column(columnDefinition = "varchar(10) COMMENT '微信行业类别编码'")
    private String wxProfCode;
    @Column(columnDefinition = "varchar(20) COMMENT '支付宝行业类别名称'")
    private String aliProfName;
    @Column(columnDefinition = "varchar(20) COMMENT '微信行业类别名称'")
    private String wxProfName;
    @Column(columnDefinition = "varchar(20) COMMENT '小程序/服务窗的appId'")
    private String appletsAppId;
    @Column(columnDefinition = "varchar(20) COMMENT '微信移动应用的appId'")
    private String mobileAppId;



    @Column(columnDefinition = "varchar(50) COMMENT '进件类型 00：企业商户 01：个人工商户  02：小微商户'")
    private String reportType;
    @Column(columnDefinition = "varchar(20) COMMENT '开户行支行名称'")
    private String accBankName;
    @Column(columnDefinition = "varchar(20) COMMENT '终端商户所在区'")
    private String countryId;
    @Column(columnDefinition = "varchar(20) COMMENT '门牌号'")
    private String buildingCode;
    @Column(columnDefinition = "varchar(20) COMMENT '路名'")
    private String road;
    @Column(columnDefinition = "varchar(20) COMMENT '营业执照编号'")
    private String licenceNo;
    @Column(columnDefinition = "varchar(20) COMMENT '法人代表证件有效期  yyyy-MM-dd'")
    private String cardDeadline;
    @Column(columnDefinition = "varchar(20) COMMENT '法人职业'")
    private String legalOccupation;
    @Column(columnDefinition = "varchar(20) COMMENT '1-男性  2-女性 5-女性改（变）为男性    6-男性改（变）为女性    9-未说明的性别'")
    private String legalSex;
    @Column(columnDefinition = "varchar(20) COMMENT '营业执照类型 0:多合一营业执照   1:普通营业执照  2:无营业执照 '")
    private String licenseType;
    @Column(columnDefinition = "varchar(20) COMMENT '请求返回的流水号'")
    private String umsReqId;
    @Column(columnDefinition = "int(2) default 0 COMMENT '验证次数'")
    private Integer verifyNum;
    @Column(columnDefinition = "int(2) default 0 COMMENT '重新发起小额鉴权次数'")
    private Integer restartNum;

    @Transient
    private Instance instance;
    @Transient
    private PaymentInfo paymentInfo;
    @Transient
    private List<Pic> picList;

    @Transient
    private Boolean aliIsEnabled = false;
    @Transient
    private Boolean wxIsEnabled  = false;
    @Transient
    private Boolean cardIsEnabled  = false;
    @Transient
    private String paymentPath;
    @Transient
    private String client;
    @Transient
    private String amount;
    @Transient
    private String verCode;
    @Transient
    private String verType;

    @Transient
    private MultipartFile attr1;
    @Transient
    private MultipartFile attr2;
    @Transient
    private MultipartFile attr3;
    @Transient
    private MultipartFile attr4;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getMerNo() {
        return merNo;
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMerNameAlias() {
        return merNameAlias;
    }

    public void setMerNameAlias(String merNameAlias) {
        this.merNameAlias = merNameAlias;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCerdId() {
        return cerdId;
    }

    public void setCerdId(String cerdId) {
        this.cerdId = cerdId;
    }

    public String getCardNoCipher() {
        return cardNoCipher;
    }

    public void setCardNoCipher(String cardNoCipher) {
        this.cardNoCipher = cardNoCipher;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getIsCompay() {
        return isCompay;
    }

    public void setIsCompay(String isCompay) {
        this.isCompay = isCompay;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getWxBankSpCode() {
        return wxBankSpCode;
    }

    public void setWxBankSpCode(String wxBankSpCode) {
        this.wxBankSpCode = wxBankSpCode;
    }

    public String getAliBankSpCode() {
        return aliBankSpCode;
    }

    public void setAliBankSpCode(String aliBankSpCode) {
        this.aliBankSpCode = aliBankSpCode;
    }

    public String getTerMerNo() {
        return terMerNo;
    }

    public void setTerMerNo(String terMerNo) {
        this.terMerNo = terMerNo;
    }

    public String getAccBankNo() {
        return accBankNo;
    }

    public void setAccBankNo(String accBankNo) {
        this.accBankNo = accBankNo;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Boolean getAliIsEnabled() {
        return aliIsEnabled;
    }

    public void setAliIsEnabled(Boolean aliIsEnabled) {
        this.aliIsEnabled = aliIsEnabled;
    }

    public Boolean getWxIsEnabled() {
        return wxIsEnabled;
    }

    public void setWxIsEnabled(Boolean wxIsEnabled) {
        this.wxIsEnabled = wxIsEnabled;
    }

    public Boolean getCardIsEnabled() {
        return cardIsEnabled;
    }

    public void setCardIsEnabled(Boolean cardIsEnabled) {
        this.cardIsEnabled = cardIsEnabled;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getOriRequestNo() {
        return oriRequestNo;
    }

    public void setOriRequestNo(String oriRequestNo) {
        this.oriRequestNo = oriRequestNo;
    }

    public String getTerReportStatus() {
        return terReportStatus;
    }

    public void setTerReportStatus(String terReportStatus) {
        this.terReportStatus = terReportStatus;
    }

    public String getAliEnabledStatus() {
        return aliEnabledStatus;
    }

    public void setAliEnabledStatus(String aliEnabledStatus) {
        this.aliEnabledStatus = aliEnabledStatus;
    }

    public String getWxEnabledStatus() {
        return wxEnabledStatus;
    }

    public void setWxEnabledStatus(String wxEnabledStatus) {
        this.wxEnabledStatus = wxEnabledStatus;
    }

    public String getAliProfCode() {
        return aliProfCode;
    }

    public void setAliProfCode(String aliProfCode) {
        this.aliProfCode = aliProfCode;
    }

    public String getWxProfCode() {
        return wxProfCode;
    }

    public void setWxProfCode(String wxProfCode) {
        this.wxProfCode = wxProfCode;
    }

    public String getAliProfName() {
        return aliProfName;
    }

    public void setAliProfName(String aliProfName) {
        this.aliProfName = aliProfName;
    }

    public String getWxProfName() {
        return wxProfName;
    }

    public void setWxProfName(String wxProfName) {
        this.wxProfName = wxProfName;
    }

    public MultipartFile getAttr1() {
        return attr1;
    }

    public void setAttr1(MultipartFile attr1) {
        this.attr1 = attr1;
    }

    public MultipartFile getAttr2() {
        return attr2;
    }

    public void setAttr2(MultipartFile attr2) {
        this.attr2 = attr2;
    }

    public MultipartFile getAttr3() {
        return attr3;
    }

    public void setAttr3(MultipartFile attr3) {
        this.attr3 = attr3;
    }

    public MultipartFile getAttr4() {
        return attr4;
    }

    public void setAttr4(MultipartFile attr4) {
        this.attr4 = attr4;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getAccBankName() {
        return accBankName;
    }

    public void setAccBankName(String accBankName) {
        this.accBankName = accBankName;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getPaymentPath() {
        return paymentPath;
    }

    public void setPaymentPath(String paymentPath) {
        this.paymentPath = paymentPath;
    }

    public List<Pic> getPicList() {
        return picList;
    }

    public void setPicList(List<Pic> picList) {
        this.picList = picList;
    }

    public String getCardDeadline() {
        return cardDeadline;
    }

    public void setCardDeadline(String cardDeadline) {
        this.cardDeadline = cardDeadline;
    }

    public String getLegalSex() {
        return legalSex;
    }

    public void setLegalSex(String legalSex) {
        this.legalSex = legalSex;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getAppletsAppId() {
        return appletsAppId;
    }

    public void setAppletsAppId(String appletsAppId) {
        this.appletsAppId = appletsAppId;
    }

    public String getLegalOccupation() {
        return legalOccupation;
    }

    public void setLegalOccupation(String legalOccupation) {
        this.legalOccupation = legalOccupation;
    }

    public String getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(String mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUmsReqId() {
        return umsReqId;
    }

    public void setUmsReqId(String umsReqId) {
        this.umsReqId = umsReqId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

    public Integer getVerifyNum() {
        return verifyNum;
    }

    public void setVerifyNum(Integer verifyNum) {
        this.verifyNum = verifyNum;
    }

    public String getVerType() {
        return verType;
    }

    public void setVerType(String verType) {
        this.verType = verType;
    }

    public Integer getRestartNum() {
        return restartNum;
    }

    public void setRestartNum(Integer restartNum) {
        this.restartNum = restartNum;
    }
}
