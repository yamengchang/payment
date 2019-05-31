package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.Instance;
import com.pay.entity.InstanceExpandUnion;

public interface InstanceExpandUnionDao extends BaseRepository<InstanceExpandUnion, String> {
    InstanceExpandUnion findByInstanceId(String instanceId);

    InstanceExpandUnion findByUnionMerId(String unionMerId);
}
