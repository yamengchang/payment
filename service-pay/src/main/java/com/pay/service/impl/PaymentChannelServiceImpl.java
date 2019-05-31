package com.pay.service.impl;

import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.PaymentChannelDao;
import com.pay.entity.PaymentChannel;
import com.pay.service.PaymentChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zwj on 2018/10/31.
 */
@Service
public class PaymentChannelServiceImpl extends BaseServiceImpl<PaymentChannel,String> implements PaymentChannelService {
    @Autowired
    private PaymentChannelDao channelDao;
    @Override
    public PaymentChannel findByInstanceIdAndChannelSign(String instanceId, String channel) {
        return channelDao.findByInstanceIdAndChannelSign(instanceId,channel);
    }

    @Override
    public List<PaymentChannel> findByInstanceId(String instanceId) {
        return channelDao.findByInstanceId(instanceId);
    }

    @Override
    public void deleteByInstanceId(String instanceId) {
        List<PaymentChannel> paymentChannelList = findByInstanceId(instanceId);
        channelDao.deleteAll(paymentChannelList);
    }
}
