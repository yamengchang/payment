package com.pay.thread;

import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.httpclient.biz.HttpClient;
import com.omv.common.util.log.LoggerUtils;

/**
 * Created by zwj on 2019/1/8.
 */
public class HttpThread implements Runnable {
    private String url;
    private Object obj;

    public HttpThread(String url, Object obj) {
        this.url = url;
        this.obj = obj;
    }

    @Override
    public void run() {
        System.out.println("=============回调参数 ："+ ValueUtil.toJson(obj)+"=============");
        for (int i = 0; i < 3; i++) {
            String result = HttpClient.conHttpByJson(url, obj,null);
            if (null != result && result.toUpperCase().indexOf("SUCCESS")>=0) {
                LoggerUtils.info("回调："+url+" 成功");
                return;
            }
            int flag = 1 << i;
            try {
                Thread.sleep(flag);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
