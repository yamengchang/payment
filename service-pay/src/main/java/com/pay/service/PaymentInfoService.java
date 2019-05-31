package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.PaymentInfo;

import java.util.List;

/**
 * Created by zwj on 2018/10/15.
 */
public interface PaymentInfoService extends BaseService<PaymentInfo,String> {
    PaymentInfo findByInstanceId(String instanceId);

}
