package com.sup.controller;

import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.date.DateUtil;
import com.omv.mongo.service.MongoService;
import com.omv.mongo.util.Page;
import com.sup.pojo.House;
import com.sup.pojo.Lease;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ChangeController {
    @Autowired
    private MongoService mongoService;

    @GetMapping(value = "/public/info/change",produces="application/json")
    public String houseChange(){
        Map<String,Object> returnMap = new LinkedHashMap<>();
        Map<String,Object> houseMap = new LinkedHashMap<>();
        Map<String,Object> leaseMap = new LinkedHashMap<>();

        Map<String,Object> params = new HashMap<>();
        params.put("pageNo","1");
        params.put("pageSize","100000000");
        params.put("createTime_lt","2019-1-26");
        Page housePage = mongoService.findAll(params,House.class,"com.sup.pojo.House");
        params.put("pageNo","1");
        params.put("pageSize","100000000");
        Page leasePage = mongoService.findAll(params, Lease.class,"com.sup.pojo.Lease");

        houseMap.put("房屋初始量",housePage.getContent().size());
        leaseMap.put("合同初始量",leasePage.getContent().size());
        params.remove("createTime_lt");
        Date initDate = DateUtil.toDate("2019-1-26","yyyy-MM-dd");
        Date nowDate = new Date();
        int chazhi = DateUtil.getDaysBetweenDate(initDate,nowDate);
        int houseChangeCount = 0;
        int leaseChangeCount = 0;

        for(int i=1;i<chazhi+1;i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(initDate);
            calendar.add(Calendar.DATE,i);
            String nextDate = DateUtil.toString(calendar.getTime(),"yyyy-MM-dd");
            System.out.println("next date is "+nextDate);

            params.put("pageNo","1");
            params.put("pageSize","100000000");
            params.put("createTime",nextDate);
            housePage = mongoService.findAll(params,House.class,"com.sup.pojo.House");
            houseMap.put(nextDate+" 变化量",housePage.getContent().size());
            houseChangeCount += housePage.getContent().size();

            params.put("pageNo","1");
            params.put("pageSize","100000000");
            params.put("createTime",nextDate);
            leasePage = mongoService.findAll(params,Lease.class,"com.sup.pojo.Lease");
            leaseMap.put(nextDate+" 变化量",leasePage.getContent().size());
            leaseChangeCount += leasePage.getContent().size();
        }
        houseMap.put("变化总量",houseChangeCount);
        leaseMap.put("变化总量",leaseChangeCount);
        returnMap.put("房源数据",houseMap);
        returnMap.put("合同数据",leaseMap);



        return ValueUtil.toJson(returnMap);
    }


}
