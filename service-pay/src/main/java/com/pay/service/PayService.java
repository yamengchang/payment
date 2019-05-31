package com.pay.service;

import com.pay.entity.BusiAccount;
import com.pay.entity.Instance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by zwj on 2018/9/26.
 */
public interface PayService {
    // 查询
    Object queryDetail(String orderId);

    Object refund(String orderNumber, String refundNumber, Double payAmount, Double refundAmount, String title, String description);
    // APP支付
    Object appPay(String orderNumber, Double amount, String title, String description, String userId, HttpServletRequest request);
    // PC消费
    Object consume(Map<String, String> params);
    //聚合支付  兴业/银联
    Object polymericPay(Map<String, Object> params) throws Exception;
    //便民支付
    Object convenientPayment(Map<String, Object> params) throws IOException;
    //便民支付批量
    Object convenientPaymentBatch(Map map) throws IOException;
    //便民账单支付状态查询
    Object convenientSearch(Map<String, Object> params) throws IOException;
    //便民账单支付状态查询批量
    Object convenientSearchBatch(Map<String, Object> params) throws IOException;
    //账单查询
    Object convenientJfBill(Map<String, Object> params) throws IOException;
    //账单查询
    Object convenientJfBillBatch(Map<String, Object> params) throws IOException;
    //建立报件
    Object createReport(BusiAccount busiAccount) throws Exception;
    //查看报件状态
    Object checkReportStatus(Instance instance, String number) throws Exception;
    //更新报件信息
    Object updateReport(BusiAccount busiAccount) throws Exception;

    Object refundTrade(Map<String, Object> params);
    //查看支付状态
    Object checkPaymentStatus(Map<String, Object> params) throws Exception;

    Object createAWReport(BusiAccount busiAccount) throws Exception;

	Object picUpload(Map<String, Object> params);

	/**
	 * ocr 验证
	 * @param params
	 * @return //		{"code":"00","msg":"成功","data":{"cardInfo":{"birthday":"19910216","address":"河南省*****号","nationality":"汉","sex":"男","name":"唐东峰","signOffice":"睢县公安局","signDate":"20180212","idNum":"41*************2162499","invalidDate":"20380212"},"respInfo":"合同已过期或未签订合同","respCode":"51"}}
	 * //		{"code":"00","msg":"成功","data":{"respInfo":"合同已过期或未签订合同","respCode":"51"}}
	 * @throws UnsupportedEncodingException
	 */
	String ocrIdCardAuth(Map<String, Object> params) throws UnsupportedEncodingException;

    Object subBusiCata(BusiAccount busiAccount) throws Exception;
}
