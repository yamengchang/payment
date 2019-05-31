package com.pay.service;

import com.omv.database.biz.BaseService;
import com.pay.entity.TradeDesc;

import java.util.Map;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
public interface TradeDescService extends BaseService<TradeDesc, Integer> {

    String getTrade(Map<String, Object> params, Integer pageNo, Integer pageSize);

}
