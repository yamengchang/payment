package com.pay.entity;

import com.omv.database.entity.BaseEntity;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zwj on 2018/10/31.
 */
@Entity
@Table(name = "tbl_ins_pay_chal")
public class PaymentChannel extends BaseEntity{
    @Column(columnDefinition = "varchar(50) COMMENT '实例id'")
    private String instanceId;
    @Column(columnDefinition = "varchar(50) COMMENT '支付渠道名称'")
    private String channelName;
    @Column(columnDefinition = "varchar(20) COMMENT '支付渠道标识   wechat_wap-公众号支付  wechat_csb-微信扫码支付  wechat_app-微信APP支付 alipay_csb-支付宝扫码支付'")
    private String channelSign;

    @Column(columnDefinition = "varchar(10) COMMENT '合同费率'")
    private String conRate;
    @Column(columnDefinition = "varchar(10) COMMENT '盈利费率'")
    private String proRate;
    @Column(columnDefinition = "varchar(10) COMMENT '银联--固定金额（分为单位）'")
    private String fixed;

    @Column(columnDefinition = "varchar(5) COMMENT '银联费率类型  02--固定收取  04--按百分比收取  05--封底收取[0.0002,50]'")
    private String unionRateType;
    @Column(columnDefinition = "varchar(10) COMMENT '银联产品类型id '")
    private String unionProdId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelSign() {
        return channelSign;
    }

    public void setChannelSign(String channelSign) {
        this.channelSign = channelSign;
    }

    public String getConRate() {
        return conRate;
    }

    public void setConRate(String conRate) {
        this.conRate = conRate;
    }

    public String getProRate() {
        return proRate;
    }

    public void setProRate(String proRate) {
        this.proRate = proRate;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getUnionRateType() {
        return unionRateType;
    }

    public void setUnionRateType(String unionRateType) {
        this.unionRateType = unionRateType;
    }

    public String getUnionProdId() {
        return unionProdId;
    }

    public void setUnionProdId(String unionProdId) {
        this.unionProdId = unionProdId;
    }
}
