package com.pay.service.impl;

import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.LeaseAccountDao;
import com.pay.entity.LeaseAccount;
import com.pay.service.LeaseAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @description: 账户实现类
 * @author: YLY
 * @create: 2018-10-10 14:22
 */
@Service
@Transactional
public class LeaseAccountServiceImpl extends BaseServiceImpl<LeaseAccount, String> implements LeaseAccountService {
    @Autowired
    private LeaseAccountDao leaseAccountDao;

    @Override
    public LeaseAccount findByUserNo(String userNo) {
        return leaseAccountDao.findByUserNo(userNo);
    }

    @Override
    public LeaseAccount findByInstanceIdAndUserId(String instanceId, String userId) {
        return leaseAccountDao.findByInstanceIdAndUserId(instanceId,userId);
    }
}
