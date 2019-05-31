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
@Table(name = "trade_desc")
public class TradeDesc extends BaseEntity {
    @Column(name = "trade_code", columnDefinition = "varchar(20) COMMENT '行业编码'")
    private String tradeCode;
    @Column(name = "trade_name", columnDefinition = "varchar(100) COMMENT '行业名称'")
    private String tradeName;
    @Column(name = "pay_channel", columnDefinition = "varchar(15) COMMENT '支付渠道'")
    private String payChannel;

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}
