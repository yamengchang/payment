package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.PaymentInfo;

import java.util.List;

/**
 * Created by zwj on 2018/10/15.
 */
public interface PaymentInfoDao extends BaseRepository<PaymentInfo, String> {
    PaymentInfo findByInstanceId(String instanceId);
}
