package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.BankName;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
public interface BankNameService extends BaseService<BankName, Integer> {

    String getBank(String bankName, String instanceId, String cityId, Integer pageNo, Integer pageSize);

}
