package com.pay.service.impl;

import com.omv.common.util.basic.ValueUtil;
import com.omv.database.bean.PageModel;
import com.omv.database.biz.BaseServiceImpl;
import com.pay.dao.TradeDescRepository;
import com.pay.entity.TradeDesc;
import com.pay.service.TradeDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Service
public class TradeDescServiceImpl extends BaseServiceImpl<TradeDesc, Integer> implements TradeDescService {

    private final TradeDescRepository tradeDescRepository;

    @Autowired
    public TradeDescServiceImpl(TradeDescRepository tradeDescRepository) {
        this.tradeDescRepository = tradeDescRepository;
    }

    @Override
    public String getTrade(Map<String, Object> params, Integer pageNo, Integer pageSize) {
        PageModel pageModel = PageModel.createPageMould(params, pageNo, pageSize);
        pageModel = super.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

}
