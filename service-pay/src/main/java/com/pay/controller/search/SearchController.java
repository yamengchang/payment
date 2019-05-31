package com.pay.controller.search;

import com.pay.service.AreaService;
import com.pay.service.BankNameService;
import com.pay.service.TradeDescService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@RestController
@Api(value = "SearchController", description = "查询")
public class SearchController {

    private final AreaService areaService;
    private final BankNameService bankNameService;
    private final TradeDescService tradeDescService;

    @Autowired
    public SearchController(AreaService areaService,
                            BankNameService bankNameService,
                            TradeDescService tradeDescService) {
        this.areaService = areaService;
        this.bankNameService = bankNameService;
        this.tradeDescService = tradeDescService;
    }

    @RequestMapping(value = "/public/area/{id}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "根据id获取下级地区", notes = "根据id获取下级地区")
    @ApiImplicitParam(required = true, name = "id", value = "区域id", paramType = "path", dataType = "int", defaultValue = "10")
    public String getArea(@PathVariable("id") int id,String instanceId) {
        return areaService.getArea(id,instanceId);
    }

    @RequestMapping(value = "/public/bank/{name}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "根据联行名称模糊查询", notes = "根据联行名称模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, name = "name", value = "联行名称", paramType = "path", dataType = "String", defaultValue = "支行"),
            @ApiImplicitParam(name = "pageNo", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int")
    })
    public String getBank(@PathVariable("name") String name,String instanceId,String cityId ,Integer pageNo, Integer pageSize) {
        return bankNameService.getBank(name, instanceId,cityId,pageNo, pageSize);
    }

    @RequestMapping(value = "/public/trade", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "获取行业列表", notes = "获取行业列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "条件"),
            @ApiImplicitParam(name = "pageNo", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int")
    })
    public String getTrade(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
        return tradeDescService.getTrade(params, pageNo, pageSize);
    }

}
