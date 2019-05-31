package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Entity
@Table(name = "bank_name")
public class BankName extends BaseEntity {

    @Column(name = "bank_no", columnDefinition = "varchar(20) COMMENT '联行号'")
    private String bankNo;
    @Column(name = "bank_name", columnDefinition = "varchar(50) COMMENT '联行名称'")
    private String bankName;
    @Column(name = "local_bank_name", columnDefinition = "varchar(50) COMMENT '本地联行名称'")
    private String localBankName;

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getLocalBankName() {
        return localBankName;
    }

    public void setLocalBankName(String localBankName) {
        this.localBankName = localBankName;
    }
}
