package com.pay.remote;

import com.pay.remote.fallback.SupervisionFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by MING on 2018/12/19.
 * Description:
 */
@Service
@FeignClient(value = "service-supervision",fallback = SupervisionFeignFallback.class)
public interface SupervisionFeign {

    @RequestMapping(value = "/validateSignature", method = RequestMethod.POST, produces = "application/json")
    String validateSignature(@RequestParam("roomId") String roomId, @RequestParam("leaseId") String leaseId);

    @RequestMapping(value = "/validateConvenientJfBill", method = RequestMethod.POST, produces = "application/json")
    String validateConvenientJfBill(@RequestParam("roomId") String roomId);

    @RequestMapping(value = "/validateConvenientPayment", method = RequestMethod.POST, produces = "application/json")
    String validateConvenientPayment(@RequestParam("roomId") String roomId, @RequestParam("leaseId") String leaseId);

    @RequestMapping(value = "/validatePolymericTrade", method = RequestMethod.POST, produces = "application/json")
    String validatePolymericTrade(@RequestParam("acctIds") String acctIds,@RequestParam("totalAmt") String totalAmt);
}
