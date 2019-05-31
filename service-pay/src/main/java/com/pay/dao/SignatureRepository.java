package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.Signature;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MING on 2019/3/8.
 * Description:
 */
@Repository
public interface SignatureRepository extends BaseRepository<Signature, String> {

    List<Signature> findAllByOrderIdAndInstanceIdAndResultOrderByCreateTimeDesc(String orderId, String instanceId, String result);
}
