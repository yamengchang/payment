package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.InstanceExpandXYV2;

public interface InstanceExpandXYV2Dao extends BaseRepository<InstanceExpandXYV2, String> {
    InstanceExpandXYV2 findByInstanceIdAndChannelType(String instanceId, String code);

}
