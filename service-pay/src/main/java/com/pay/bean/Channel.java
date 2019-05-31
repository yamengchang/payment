package com.pay.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwj on 2018/9/26.
 */
public  enum Channel {

    XY,Union,XY_V2;

    private static Map<String, Channel> map = new HashMap<>();

    static {
        Channel[] channels = Channel.values();
        for (int i = 0; i < channels.length; i++) {
            map.put(channels[i].name(), channels[i]);
        }
    }

    public static Channel getChannel(String name) {
        return map.get(name);
    }


}
