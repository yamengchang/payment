package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.InstanceExpandXYV1;

public interface InstanceExpandXYV1Dao extends BaseRepository<InstanceExpandXYV1, String> {
    InstanceExpandXYV1 findByInstanceId(String instanceId);

    InstanceExpandXYV1 findByMrchId(String mrchId);
}
