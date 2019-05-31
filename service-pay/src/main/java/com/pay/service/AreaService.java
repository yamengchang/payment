package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.Area;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
public interface AreaService extends BaseService<Area, Integer> {

    String getArea(int areaId, String instanceId);

}
