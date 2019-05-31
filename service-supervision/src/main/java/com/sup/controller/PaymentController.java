package com.sup.controller;

import com.omv.common.util.basic.ValueUtil;
import com.sup.pojo.Accounting;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zwj on 2018/12/17.
 */
@RestController
public class PaymentController {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public PaymentController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping(value = "/validateSignature", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "发起签约验证", notes = "发起签约验证")
    public String validateSignature(@RequestParam String roomId, @RequestParam String leaseId) {
        Query house = new Query(Criteria.where("_id").is(roomId));
        if (!mongoTemplate.exists(house, "com.sup.pojo.House")) {
            return ValueUtil.toJson("房源不存在");
        }
        Query lease = new Query(Criteria.where("_id").is(leaseId));
        if (!mongoTemplate.exists(lease, "com.sup.pojo.Lease")) {
            return ValueUtil.toJson("合同不存在");
        }
        return ValueUtil.toJson();
    }

    @RequestMapping(value = "/validateConvenientJfBill", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "便民账单查看验证", notes = "便民账单查看验证")
    public String validateConvenientJfBill(@RequestParam String roomId) {
        Query house = new Query(Criteria.where("_id").is(roomId));
        if (!mongoTemplate.exists(house, "com.sup.pojo.House")) {
            return ValueUtil.toJson("房源不存在");
        }
        return ValueUtil.toJson();
    }

    @RequestMapping(value = "/validateConvenientPayment", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "便民缴费验证", notes = "便民缴费验证")
    public String validateConvenientPayment(@RequestParam String roomId, @RequestParam String leaseId) {
        Query house = new Query(Criteria.where("_id").is(roomId));
        if (!mongoTemplate.exists(house, "com.sup.pojo.House")) {
            return ValueUtil.toJson("房源不存在");
        }
        Query lease = new Query(Criteria.where("_id").is(leaseId));
        if (!mongoTemplate.exists(lease, "com.sup.pojo.Lease")) {
            return ValueUtil.toJson("合同不存在");
        }
        return ValueUtil.toJson();
    }

    @RequestMapping(value = "/validatePolymericTrade", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "聚合支付验证", notes = "聚合支付验证")
    public String validatePolymericTrade(String acctIds,String totalAmt) {
        String[] acctIdArr = acctIds.split(",");
        Criteria criteria = new Criteria();
        criteria.and("_id").in(acctIdArr);
        Query acctQuery = new Query(criteria);
        List<Accounting> accountingList = mongoTemplate.find(acctQuery,Accounting.class ,"com.sup.pojo.Lease");
        if (accountingList.size() != acctIdArr.length) {
            return ValueUtil.toError("500","包含不存在的账单");
        }
//        if (!mongoTemplate.exists(lease, "com.sup.pojo.Lease")) {
//            return ValueUtil.toJson("合同不存在");
//        }
        return ValueUtil.toJson();
    }

}
