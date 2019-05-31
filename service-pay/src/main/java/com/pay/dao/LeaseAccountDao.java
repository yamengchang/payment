package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.LeaseAccount;
import org.springframework.stereotype.Repository;

/**
 * @description: 账户DAO
 * @author: YLY
 * @create: 2018-10-10 14:22
 */
@Repository
public interface LeaseAccountDao extends BaseRepository<LeaseAccount, String> {

    LeaseAccount findByUserId(String userId);

    LeaseAccount findByUserNo(String userNo);

    LeaseAccount findByInstanceIdAndUserId(String instanceId, String userId);
}
