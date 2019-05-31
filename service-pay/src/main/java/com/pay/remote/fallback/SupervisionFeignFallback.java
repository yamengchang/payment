package com.pay.remote.fallback;

import com.omv.common.util.basic.ValueUtil;
import com.pay.remote.SupervisionFeign;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by MING on 2018/12/19.
 * Description:
 */
@Component
public class SupervisionFeignFallback implements FallbackFactory<SupervisionFeign> {
    @Override
    public SupervisionFeign create(Throwable cause) {
        return new SupervisionFeignFallbackFactory() {
            @Override
            public String validateSignature(String roomId, String leaseId) {
                return ValueUtil.toJson(cause.getMessage());
            }

            @Override
            public String validateConvenientJfBill(String roomId) {
                return null;
            }

            @Override
            public String validateConvenientPayment(String roomId, String leaseId) {
                return null;
            }

            @Override
            public String validatePolymericTrade(String leaseId, String totalAmt) {
                return null;
            }
        };
    }
}
