package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.BusiAccountChild;

public interface BusiAccountChildDao extends BaseRepository<BusiAccountChild,String> {
    BusiAccountChild findByInstanceIdAndNumber(String instanceId, String number);
}
