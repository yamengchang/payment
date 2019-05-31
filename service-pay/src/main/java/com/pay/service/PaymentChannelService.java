package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.PaymentChannel;

import java.util.List;

/**
 * Created by zwj on 2018/10/31.
 */
public interface PaymentChannelService extends BaseService<PaymentChannel,String> {
    PaymentChannel findByInstanceIdAndChannelSign(String instanceId, String channel);

    List<PaymentChannel> findByInstanceId(String instanceId);

    void deleteByInstanceId(String instanceId);
}
