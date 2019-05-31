package com.sup.service.impl;

import com.sup.constant.*;
import com.sup.pojo.*;
import com.sup.service.*;
import org.apache.commons.lang.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.stereotype.*;

/**
 * @author tdf
 * @date 2018-12-19 10:08
 **/
@Service
public class HouseMongoServiceImpl extends BaseMongoDbServiceImpl<House> implements IHouseMongoService {

	@Autowired
	private MongoTemplate mongoTemplate;

	HouseMongoServiceImpl() {
		super(House.class);
	}

	@Override
	public String save(House house) {
		//判断是不是业主
		if (StringUtils.isNotEmpty(house.getRoomCertCode()) && StringUtils.isNotEmpty(house.getHouseNo()) && StringUtils.isNotEmpty(house.getRoomOwnerName()) && StringUtils.isNotEmpty(house.getRoomCode())) {
			house.setRoomIsOwner(Status.isRoomOwner);
		}else {
			house.setRoomIsOwner(Status.notRoomOwner);
		}
		return super.save(house);
	}

}
