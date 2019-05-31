package com.pay.service.impl;

import com.omv.common.util.basic.ValueUtil;
import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.AreaRepository;
import com.pay.entity.Area;
import com.pay.entity.Instance;
import com.pay.service.AreaService;
import com.pay.service.InstanceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, Integer> implements AreaService {

    private final AreaRepository areaRepository;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    public AreaServiceImpl(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Override
    public String getArea(int areaId, String instanceId) {
        if(StringUtils.isNotEmpty(instanceId)){
            Instance instance = instanceService.findByInstanceId(instanceId);
            if(instance == null){
                ValueUtil.isError("实例信息不存在");
            }

        }else{
            return getUnionArea(areaId);
        }
        return null;
    }


    private String getUnionArea(int areaId) {
        List<Area> areaList = areaRepository.findAllByAreaId(areaId);
        if (areaList.isEmpty()) {
            ValueUtil.isError("地区不存在");
        }
        Area area = areaList.get(0);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        areaList = areaRepository.findAllByParentId(areaId);
        for (Area ar : areaList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ar.getAreaId());
            map.put("name", ar.getAreaName());
            list.add(map);
        }
        result.put("id", area.getAreaId());
        result.put("name", area.getAreaName());
        result.put("data", list);
        return ValueUtil.toJson(result);
    }
}
