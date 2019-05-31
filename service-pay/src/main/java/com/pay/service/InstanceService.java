package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.Instance;
import com.pay.entity.PaymentInfo;

import java.io.IOException;
import java.util.*;

/**
 * Created by zwj on 2018/9/30.
 */
public interface InstanceService extends BaseService<Instance,String> {

    Instance findByInstanceId(String instanceKey);

    Instance findByInstanceIdAndType(String instanceKey,String type);

    void saveInstance(Instance instance) throws Exception;

    void perfectedPayment(Instance instance) throws IOException;

	List<Instance> getAllXY();
}
