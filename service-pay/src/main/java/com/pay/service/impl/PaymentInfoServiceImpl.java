package com.pay.service.impl;

import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.PaymentInfoDao;
import com.pay.entity.PaymentInfo;
import com.pay.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zwj on 2018/10/15.
 */
@Service
public class PaymentInfoServiceImpl extends BaseServiceImpl<PaymentInfo,String> implements PaymentInfoService {
    @Autowired
    private PaymentInfoDao paymentInfoDao;
    @Override
    public PaymentInfo findByInstanceId(String instanceId) {
        return paymentInfoDao.findByInstanceId(instanceId);
    }

}
