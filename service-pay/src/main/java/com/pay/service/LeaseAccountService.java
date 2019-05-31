package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.LeaseAccount;

/**
 * @description: 账户接口类
 * @author: YLY
 * @create: 2018-10-10 14:25
 */
public interface LeaseAccountService extends BaseService<LeaseAccount,String> {
    LeaseAccount findByUserNo(String userNo);

    LeaseAccount findByInstanceIdAndUserId(String instanceId, String userId);
}
