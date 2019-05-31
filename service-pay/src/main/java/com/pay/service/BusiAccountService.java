package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.BusiAccount;

/**
 * Created by zwj on 2018/10/30.
 */
public interface BusiAccountService  extends BaseService<BusiAccount,String> {
    BusiAccount findByInstanceIdAndNumber(String instanceId, String number);
}
