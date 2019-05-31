package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_busi_account_child")
public class BusiAccountChild extends BaseEntity {
    @Column(columnDefinition = "varchar(32) COMMENT '实例id，平台唯一'")
    private String instanceId;
    @Column(columnDefinition = "varchar(8) COMMENT '第三方平台商户唯一识别号，必须是8位的数字(8位数字仅限于银联)'")
    private String number;
    @Column(columnDefinition = "varchar(32) COMMENT '用于支付的子商户号，平台唯一'")
    private String childMid;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getChildMid() {
        return childMid;
    }

    public void setChildMid(String childMid) {
        this.childMid = childMid;
    }
}
