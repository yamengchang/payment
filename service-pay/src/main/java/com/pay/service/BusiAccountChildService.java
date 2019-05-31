package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.BusiAccountChild;

public interface BusiAccountChildService extends BaseService<BusiAccountChild,String> {
    BusiAccountChild findByInstanceIdAndNumber(String instanceId, String number);
}