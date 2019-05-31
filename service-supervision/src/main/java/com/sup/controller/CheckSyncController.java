package com.sup.controller;

import com.omv.common.util.basic.ValueUtil;
import com.sup.pojo.Accounting;
import com.sup.pojo.House;
import com.sup.pojo.Lease;
import com.sup.pojo.ReturnEntity;
import com.sup.service.IAccountingMongleService;
import com.sup.service.IHouseMongoService;
import com.sup.service.ILeaseMongleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zwj on 2019/1/21.
 */
@RestController
public class CheckSyncController {
    @Autowired
    private ILeaseMongleService leaseMongleService;

    @Autowired
    private IAccountingMongleService iAccountingMongleService;

    @Autowired
    private IHouseMongoService iHouseMongoService;

    @GetMapping("/scrt/sync/data/status")
    public String checkSyncController(@RequestBody Map<String,String> params){
        ValueUtil.verifyParams("instanceId,thirdIds,dataType",params);
        String instanceId = params.get("instanceId");
        String dataType = params.get("dataType");
        String thirdIds = params.get("thirdIds");
        String[] thirdIdArr = thirdIds.split(",");
        List<ReturnEntity> returnList = new ArrayList<>(thirdIdArr.length);
        if(dataType.equals("house")){
            for (String thirdId : thirdIdArr) {
                String id = instanceId+thirdId;
                House dbHouse = (House) iHouseMongoService.findById(id);//判断该数据是否存在，若存在则修改
                if (null == dbHouse) {
                    returnList.add(ReturnEntity.error(null,thirdId,"不存在"));
                }else{
                    returnList.add(ReturnEntity.success(id,thirdId));
                }
            }
        }else if(dataType.equals("lease")){
            for (String thirdId : thirdIdArr) {
                String id = instanceId+thirdId;
                Lease dbLease = (Lease) leaseMongleService.findById(id);//判断该数据是否存在，若存在则修改
                if (null == dbLease) {
                    returnList.add(ReturnEntity.error(null,thirdId,"不存在"));
                }else{
                    returnList.add(ReturnEntity.success(id,thirdId));
                }
            }
        }else if(dataType.equals("acct")){
            for (String thirdId : thirdIdArr) {
                String id = instanceId+thirdId;
                Accounting dbAccounting = (Accounting) iAccountingMongleService.findById(id);//判断该数据是否存在，若存在则修改
                if (null == dbAccounting) {
                    returnList.add(ReturnEntity.error(null,thirdId,"不存在"));
                }else{
                    returnList.add(ReturnEntity.success(id,thirdId));
                }
            }
        }else{
            ValueUtil.isError("dataType：数据类型错误");
        }

        return ValueUtil.toJson(returnList);
    }
}
