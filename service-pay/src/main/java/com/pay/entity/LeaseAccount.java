package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/*
* 租赁用户账号表
* */
@Entity
@Table(name = "tbl_lease_account")
public class LeaseAccount extends BaseEntity {
    @Column(columnDefinition = "varchar(64) COMMENT '用户id，平台唯一'")
    private String userId;
    @Column(columnDefinition = "varchar(64) COMMENT '用户编号，平台唯一'")
    private String userNo;
    @Column(columnDefinition = "varchar(11) COMMENT '余额'")
    private String balance;
    @Column(columnDefinition = "varchar(11) COMMENT '冻结金额'")
    private String freezeAmount;
    @Column(columnDefinition = "varchar(11) COMMENT '总额'")
    private String countAmount;
    @Column(columnDefinition = "varchar(2) COMMENT '账号状态 0-开户中   1-开户成功  2-开户失败  3-绑卡成功'")
    private String status;
    @Column(columnDefinition = "varchar(50) COMMENT '实例Id'")
    private String instanceId;
    @Column(columnDefinition = "varchar(50) COMMENT '虚拟账户'")
    private String virtualAccount;
    @Column(columnDefinition = "varchar(50) COMMENT '实体账户'")
    private String account;
    @Column(columnDefinition = "varchar(50) COMMENT '实体账户名'")
    private String accountName;
    @Column(columnDefinition = "varchar(11) COMMENT '小额鉴定剩余次数'")
    private String litAmtAuthNum;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFreezeAmount() {
        return freezeAmount;
    }

    public void setFreezeAmount(String freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    public String getCountAmount() {
        return countAmount;
    }

    public void setCountAmount(String countAmount) {
        this.countAmount = countAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getVirtualAccount() {
        return virtualAccount;
    }

    public void setVirtualAccount(String virtualAccount) {
        this.virtualAccount = virtualAccount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getLitAmtAuthNum() {
        return litAmtAuthNum;
    }

    public void setLitAmtAuthNum(String litAmtAuthNum) {
        this.litAmtAuthNum = litAmtAuthNum;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


}
