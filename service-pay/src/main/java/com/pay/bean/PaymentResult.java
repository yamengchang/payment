package com.pay.bean;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
public class PaymentResult {

    private String order_no;//平台订单号
    private String status;//支付状态 PROCESSING-处理中，SUCCESS-成功，FAILED-失败、REVOKED 已撤销、REFUND已退款
    private String time_paid;//支付完成时间
    private String pay_order_no;//支付单号
    private String transaction_no;//流水号
    private String mrch_id;//商户号
    private String amount;//支付金额

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime_paid() {
        return time_paid;
    }

    public void setTime_paid(String time_paid) {
        this.time_paid = time_paid;
    }

    public String getPay_order_no() {
        return pay_order_no;
    }

    public void setPay_order_no(String pay_order_no) {
        this.pay_order_no = pay_order_no;
    }

    public String getTransaction_no() {
        return transaction_no;
    }

    public void setTransaction_no(String transaction_no) {
        this.transaction_no = transaction_no;
    }

    public String getMrch_id() {
        return mrch_id;
    }

    public void setMrch_id(String mrch_id) {
        this.mrch_id = mrch_id;
    }
}
