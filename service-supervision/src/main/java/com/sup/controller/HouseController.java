package com.sup.controller;

import com.sup.pojo.*;
import com.sup.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

/**
 * @author tdf
 * @date 2018-12-19 10:06
 **/
@RestController
@RequestMapping("/scrt/data/house")
@Api(value = "HouseController", description = "房源", tags = {"房源"})
public class HouseController extends BaseController<House> {
	@Autowired
	public HouseController(IHouseMongoService iHouseMongoService) {
		super.baseMongoDbService = iHouseMongoService;
		super.aClass = House.class;
	}


}
