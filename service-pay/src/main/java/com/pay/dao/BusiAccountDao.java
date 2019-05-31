package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.BusiAccount;
import com.pay.entity.Instance;

/**
 * Created by zwj on 2018/10/30.
 */
public interface BusiAccountDao extends BaseRepository<BusiAccount,String> {
    BusiAccount findByInstanceIdAndNumber(String instanceId, String number);
}
