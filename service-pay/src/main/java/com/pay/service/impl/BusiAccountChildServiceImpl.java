package com.pay.service.impl;

import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.BusiAccountChildDao;
import com.pay.entity.BusiAccountChild;
import com.pay.service.BusiAccountChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusiAccountChildServiceImpl extends BaseServiceImpl<BusiAccountChild, String> implements BusiAccountChildService {
    @Autowired
    private BusiAccountChildDao busiAccountChildDao;

    @Override
    public BusiAccountChild findByInstanceIdAndNumber(String instanceId, String number) {
        return busiAccountChildDao.findByInstanceIdAndNumber(instanceId, number);
    }
}
