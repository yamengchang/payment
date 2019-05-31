package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by MING on 2019/3/8.
 * Description:
 */
@Entity
@Table(name = "tbl_signature")
public class Signature extends BaseEntity {
    @Column(columnDefinition = "VARCHAR(30) COMMENT '订单号'")
    private String orderId;
    @Column(columnDefinition = "VARCHAR(30) COMMENT '实例ID'")
    private String instanceId;
    @Column(columnDefinition = "VARCHAR(30) COMMENT '机构码'")
    private String insCode;
    @Column(columnDefinition = "VARCHAR(30) COMMENT '合同模板号'")
    private String templateNo;
    @Column(columnDefinition = "VARCHAR(30) COMMENT '签约编号'")
    private String signNo;
    @Column(columnDefinition = "VARCHAR(50) COMMENT '发起时间'")
    private String invokeTimestamp;
    @Column(columnDefinition = "VARCHAR(3000) COMMENT '合同要素'")
    private String contractElement;
    @Column(columnDefinition = "VARCHAR(3000) COMMENT '签约结果'")
    private String resultMsg;
    @Column(columnDefinition = "VARCHAR(3000) COMMENT '签约结果 0-成功 1-失败'")
    private String result;

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

    public String getInsCode() {
        return insCode;
    }

    public void setInsCode(String insCode) {
        this.insCode = insCode;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getSignNo() {
        return signNo;
    }

    public void setSignNo(String signNo) {
        this.signNo = signNo;
    }

    public String getInvokeTimestamp() {
        return invokeTimestamp;
    }

    public void setInvokeTimestamp(String invokeTimestamp) {
        this.invokeTimestamp = invokeTimestamp;
    }

    public String getContractElement() {
        return contractElement;
    }

    public void setContractElement(String contractElement) {
        this.contractElement = contractElement;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
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
