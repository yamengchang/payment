package com.sup.service.impl;

import com.sup.pojo.*;
import com.sup.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * @author tdf
 * @date 2018-12-18 10:13
 **/
@Service
public class LeaseMongleServiceImpl extends BaseMongoDbServiceImpl<Lease> implements ILeaseMongleService {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	public LeaseMongleServiceImpl() {
		super(Lease.class);
	}
}
