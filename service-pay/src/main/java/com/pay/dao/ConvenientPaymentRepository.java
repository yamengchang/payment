package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.ConvenientPayment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MING on 2019/3/6.
 * Description:
 */
@Repository
public interface ConvenientPaymentRepository extends BaseRepository<ConvenientPayment, String> {

    List<ConvenientPayment> findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(String orderId, String instanceId, String result);
}
