package com.pay.service.impl;

import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.BusiAccountDao;
import com.pay.entity.BusiAccount;
import com.pay.service.BusiAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zwj on 2018/10/30.
 */
@Service
public class BusiAccountServiceImpl  extends BaseServiceImpl<BusiAccount, String> implements BusiAccountService {
    @Autowired
    private BusiAccountDao busiAccountDao;
    @Override
    public BusiAccount findByInstanceIdAndNumber(String instanceId, String number) {
        return busiAccountDao.findByInstanceIdAndNumber(instanceId, number);
    }
}
