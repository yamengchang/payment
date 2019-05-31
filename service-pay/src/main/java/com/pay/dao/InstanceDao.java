package com.pay.dao;

import com.omv.database.repository.*;
import com.pay.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Created by zwj on 2018/9/30.
 */
@Repository
public interface InstanceDao extends BaseRepository<Instance, String> {
	Instance findByInstanceId(String instanceId);

	List<Instance> findByInstanceIdInAndType(List<String> instanceIdList, String type);

	@Query(value = "SELECT * FROM tbl_instance WHERE TYPE='0' AND channel='XY' AND STATUS='1'", nativeQuery = true)
	List<Instance> getAllXY();

    Instance findByInstanceIdAndType(String instanceKey, String type);
}
