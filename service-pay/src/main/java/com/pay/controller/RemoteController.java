package com.pay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by zwj on 2018/12/5.
 */
@RestController
public class RemoteController {
    @PostMapping("/dist/busi")
    public String distributionBusiness(@RequestParam Map<String,String> params){
        String busi = params.get("busi");
        if(busi.equals("0000")){//企业证书颁发

        }else if(busi.equals("0001")){//个人证书颁发

        }else if(busi.equals("0001")){//个人证书颁发

        }else if(busi.equals("0002")){//发起签约

        }else if(busi.equals("0003")){//签约状态查询

        }else if(busi.equals("0004")){//签约下载

        }else if(busi.equals("0010")){//个人缴费账单查看

        }else if(busi.equals("0011")){//发起缴费

        }else if(busi.equals("0020")){//发起付款

        }else if(busi.equals("0021")){//付款状态查询

        }
        return null;
    }
}
