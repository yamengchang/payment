package com.sup.listener;

import com.alibaba.fastjson.*;
import com.omv.common.util.basic.*;
import com.omv.common.util.error.CustomException;
import com.omv.common.util.httpclient.biz.*;
import com.omv.common.util.log.LoggerUtils;
import com.omv.common.util.treadPool.*;
import com.omv.mongo.util.*;
import com.sup.pojo.*;
import com.sup.remote.*;
import com.sup.service.*;
import com.sup.thread.*;
import com.sup.util.*;
import org.apache.commons.logging.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.util.*;

/**
 * Created by zwj on 2018/12/17.
 * 数据处理监听器
 */
@Component
public class HouseDataDealWithListener {
    private static final Log logger = LogFactory.getLog(HouseDataDealWithListener.class);
    @Autowired
    private PayRemote payRemote;
    @Autowired
    private IHouseMongoService iHouseMongoService;
    private Class<House> aClass = House.class;
    private static final String name = "House";


    @RabbitListener(queues = name + "-save")
    public Object save(String message) {
        Assert.notNull(message, "message 不能为空");
        House house = JSONObject.parseObject(message, aClass);

//        House dbHouse = (House) iHouseMongoService.findById(house.getId());//判断该数据是否存在，若存在则修改
        Map<String,Object> params = new HashMap<>();
        params.put("instanceId",house.getInstanceId());
        params.put("thirdNo",house.getThirdNo());
        List dbList = iHouseMongoService.findAll(params).getContent();//判断该数据是否存在，若存在则修改

        if (null != dbList&&dbList.size()>0) {
            House dbHouse = (House)( dbList.get(0));
            house.setLastUpdateTime(new Date());
            house.setCreateTime(dbHouse.getCreateTime());
            return iHouseMongoService.update(house);
        }

        if(house.getIsUpstream()!=null&&house.getIsUpstream().equals("1")){//是上游房源信息
            house.setIsDownStream("0");
            if(house.getBuildType().equals("0")){//分散式
                ValueUtil.verifyParams("thirdNo,name,provName,cityName,countyName,address," +
                        "buildingCode,floorCount,isElevator,unitNo,houseNo,floor,houseSpace," +
                        "roomNum,parlourNum,toiletNum,houseType" ,house);
            }else{//集中式
                ValueUtil.verifyParams("thirdNo,name,provName,cityName,countyName,address," +
                        "buildingCode,floorCount,isElevator,houseType" ,house);
            }


        }else{
            house.setIsUpstream("0");
            house.setIsDownStream("1");
            ValueUtil.verifyParams("thirdNo,name,provName,cityName,countyName,address," +
                    "buildingCode,floorCount,isElevator,unitNo,houseNo,floor,houseSpace," +
                    "buildType,leaseType,roomNum,parlourNum,toiletNum,houseType,roomSpace," +
                    "leaseStatus,maintenanceCost,waterCost,electricityCost,gasCost,airConditioningCost",house);

            if(house.getLeaseType().equals("1")&&StringUtils.isEmpty(house.getHouseId())){//整租
                house.setHouseId(house.getThirdNo());
            }
        }

        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("address", house.getAddress());
        map.put("cityName", house.getCityName());
        map.put("countyName", house.getCountyName());
        map.put("provName", house.getProvName());
        map.put("name", house.getName());
        if(house.getIsUpstream()!=null&&house.getIsUpstream().equals("1")) {//是上游房源信息
            map.put("isUpstream","1");
        }else{
            map.put("isUpstream","0");
        }

        Page house1 = iHouseMongoService.findAll(map);
        //没有找到其他平台的相同数据
        if (house1.getTotalRows() == 0) {
            return iHouseMongoService.save(house);
        } else {
            //   找到其他平台的相同数据
            List<House> house2 = (List<House>) house1.getContent();

            // 取第一条的snamehouseIds 和对应的id作为 SameHouseIds  进行保存
            house.setSameHouseIds((house2.get(0).getSameHouseIds() == null ? "" : house2.get(0).getSameHouseIds() + ",") + house2.get(0).getId());
            String id = iHouseMongoService.save(house);
            List<House> houses = new ArrayList<>(house2.size());
            //更新相同数据的sanemhouseid，加上本次更新的数据
            for (House house3 : house2) {
                house3.setSameHouseIds((house3.getSameHouseIds() == null ? "" : house3.getSameHouseIds() + ",") + id);
                houses.add(house3);
            }
            return iHouseMongoService.batchUpdate(houses);
        }
    }

    @RabbitListener(queues = name + "-batchsave")
    public void batchSave(String message) {
        ReceiveEntity receiveEntity = JSONObject.parseObject(message, ReceiveEntity.class);
        try {
            String instanceId = receiveEntity.getInstanceId();
            String notifyUrl = receiveEntity.getNotifyUrl();
            String instanceInfo = payRemote.instanceInfo(instanceId);
            List dataList = DataDecodeUtil.getData(instanceInfo, receiveEntity);

            List<ReturnEntity> returnEntityList = new ArrayList<>(dataList.size());
            for (Object obj : dataList) {
                House house = (House) MapUtil.mapToObject( (Map) obj,House.class);
                house.setInstanceId(instanceId);
                try {
                    house.setId(IDUtil.getID());
                    save(JSONObject.toJSONString(house));
                    returnEntityList.add(ReturnEntity.success(house.getId(), house.getThirdNo()));
                } catch (Exception e) {
                    if(e instanceof CustomException){
                        returnEntityList.add(ReturnEntity.error(null, house.getThirdNo(),e.getMessage()));
                    }else{
                        e.printStackTrace();
                        returnEntityList.add(ReturnEntity.error(null, house.getThirdNo(),"保存失败"));
                    }
                }
            }
            LoggerUtils.info(receiveEntity.getTimestamp()+" 房屋数据处理完成，开始回调");
            ThreadPoolUtils.addFixedThread(new HttpThread(notifyUrl, returnEntityList));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @RabbitListener(queues = name + "-update")
    public void update(String message) {
        Assert.notNull(message, "message 不能为空");
        iHouseMongoService.save(JSONObject.parseObject(message, aClass));

    }

}
