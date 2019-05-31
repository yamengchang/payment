package com.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.omv.common.util.basic.IDUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.date.DateUtil;
import com.omv.database.bean.PageModel;
import com.omv.database.biz.BaseServiceImpl;
import com.pay.bean.Channel;
import com.pay.bean.XYPayChannelEnum;
import com.pay.dao.InstanceExpandXYV2Dao;
import com.pay.entity.BankName;
import com.pay.entity.Instance;
import com.pay.entity.InstanceExpandXYV2;
import com.pay.service.BankNameService;
import com.pay.service.InstanceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Service
public class BankNameServiceImpl extends BaseServiceImpl<BankName, Integer> implements BankNameService {
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private InstanceExpandXYV2Dao instanceExpandXYV2Dao;

    /*  start  商户进件配置  start   */
    @Value("${xy.v2.report.url}")
    private String reportUrl;
    /*  end  商户进件配置  end   */

    @Override
    public String getBank(String bankName, String instanceId, String cityId, Integer pageNo, Integer pageSize) {
        if(StringUtils.isNotEmpty(instanceId)){
            Instance instance = instanceService.findByInstanceId(instanceId);
            if(instance == null){
                ValueUtil.isError("实例信息不存在");
            }
        }else{
            return getUnionBankNo(bankName,pageNo,pageSize);
        }
        return null;
    }


    private String getUnionBankNo(String bankName, Integer pageNo, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("bankName_l", bankName);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        PageModel pageModel = PageModel.createPageMould(params, pageNo, pageSize);
        pageModel = super.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }
}
