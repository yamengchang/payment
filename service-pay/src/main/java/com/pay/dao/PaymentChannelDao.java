package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.PaymentChannel;

import java.util.List;

/**
 * Created by zwj on 2018/10/31.
 */
public interface PaymentChannelDao  extends BaseRepository<PaymentChannel,String> {
    PaymentChannel findByInstanceIdAndChannelSign(String instanceId, String channel);

    List<PaymentChannel> findByInstanceId(String instanceId);
}
