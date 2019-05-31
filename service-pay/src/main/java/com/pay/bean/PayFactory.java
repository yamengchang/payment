package com.pay.bean;

import com.omv.common.util.basic.ValueUtil;
import com.pay.service.PayService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Component
public class PayFactory {

    @Autowired
    private BeanFactory beanFactory;

    private static Map<Channel, Class> allInstance = new HashMap<>();

    //                 code    classPath
    private static Map<Channel, String> allInstanceR = new HashMap<>();

    private static Map<Channel, Boolean> instanceStatus = new HashMap<>();


    static {
        allInstanceR.clear();
        allInstanceR.put(Channel.Union, "com.pay.service.impl.UnionPayServiceImpl");

        allInstance.clear();
        for (Map.Entry<Channel, String> entry : allInstanceR.entrySet()) {
            try {
                Class impl = Class.forName(entry.getValue());
                allInstance.put(entry.getKey(), impl);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        refreshChannel();
    }


    public static Object refreshChannel() {
        instanceStatus.put(Channel.XY, true);
        return instanceStatus;
    }

    public PayService getInstance(Channel channel)  {//得到一个实例
        PayService payService = null;
        payService = (PayService) beanFactory.getBean(allInstance.get(channel));
        return payService;
    }

}
