package com.pay.controller.signature;

import com.omv.common.util.basic.ValueUtil;
import com.pay.bean.Constants;
import com.pay.bean.InstanceTypeEnum;
import com.pay.entity.Instance;
import com.pay.remote.SupervisionFeign;
import com.pay.service.InstanceService;
import com.pay.service.SignatureService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by MING on 2018/11/9.
 * Description:
 */
@RestController
public class SignatureController {

    private final SignatureService signatureService;
    private final InstanceService instanceService;
    private final Constants constants;
    private final SupervisionFeign supervisionFeign;

    @Autowired
    public SignatureController(SignatureService signatureService, InstanceService instanceService, Constants constants, SupervisionFeign supervisionFeign) {
        this.signatureService = signatureService;
        this.instanceService = instanceService;
        this.constants = constants;
        this.supervisionFeign = supervisionFeign;
    }

    @RequestMapping(value = "/scrt/signature/authorization", method = RequestMethod.GET)
    @ApiOperation(value = "获取授权码", notes = "获取授权码")
    public void authorization() {
        try {
            signatureService.authorization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping(value = "/scrt/signature/certificate/ent", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "企业证书颁发", notes = "企业证书颁发")
    public String certificateEnterprise(@RequestParam Map<String, String> params) throws Exception {
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            i++;
            System.out.println(i + ":" + entry.getKey() + "," + entry.getValue());
        }
        Instance instance = instanceService.findByInstanceIdAndType(params.get("instanceId"), InstanceTypeEnum.SIGNATURE.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }

        return ValueUtil.toJson(signatureService.certificateEnterprise(params));
    }

    @RequestMapping(value = "/scrt/signature/certificate/per", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "个人证书颁发", notes = "企业证书颁发")
    public String certificatePerson(@RequestParam Map<String, String> params) throws Exception {
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            i++;
            System.out.println(i + ":" + entry.getKey() + "," + entry.getValue());
        }
        Instance instance = instanceService.findByInstanceIdAndType(params.get("instanceId"), InstanceTypeEnum.SIGNATURE.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        return ValueUtil.toJson(signatureService.certificatePerson(params));
    }

    @RequestMapping(value = "/scrt/signature/start", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "发起签约", notes = "发起签约")
    public String start(@RequestParam Map<String, Object> params) throws Exception {
        //房协验证...验证开始...
        if (constants.getHouseVerifyEnable()) {

            ValueUtil.verifyParams("roomId,leaseId", params.get("roomId"), params.get("leaseId"));
            String roomId = params.get("roomId") != null ? params.get("roomId").toString() : null;
            String leaseId = params.get("leaseId") != null ? params.get("leaseId").toString() : null;
            String res = supervisionFeign.validateSignature(roomId, leaseId);
            if (res == null) {
                ValueUtil.isError("数据不存在");
            }
            if (!"".equals(ValueUtil.getFromJson(res, "data"))) {
                ValueUtil.isError(ValueUtil.getFromJson(res, "data", "content"));
            }
        }
        //...验证结束...
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            i++;
            System.out.println(i + ":" + entry.getKey() + "," + entry.getValue());
        }
        Instance instance = instanceService.findByInstanceIdAndType((String) params.get("instanceId"), InstanceTypeEnum.SIGNATURE.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        return ValueUtil.toJson(signatureService.start(params));
    }

    @RequestMapping(value = "/scrt/signature/query", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "签约查询", notes = "签约查询")
    public String query(@RequestParam String orderId, Integer page, Integer rows, String instanceId) throws Exception {
        Instance instance = instanceService.findByInstanceIdAndType(instanceId, InstanceTypeEnum.SIGNATURE.getCode());
//        Instance instance = instanceService.findByInstanceId(instanceId);
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        return ValueUtil.toJson(signatureService.query(orderId, instanceId, page, rows));
    }

    @RequestMapping(value = "/scrt/signature/download", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "签约下载", notes = "签约下载")
    public String download(@RequestParam String orderId, @RequestParam String type, @RequestParam String instanceId, HttpServletResponse response) throws Exception {
        Instance instance = instanceService.findByInstanceIdAndType(instanceId, InstanceTypeEnum.SIGNATURE.getCode());
//        Instance instance = instanceService.findByInstanceId(instanceId);
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        return signatureService.download(orderId, type, instanceId, response);
    }

    @RequestMapping(value = "/scrt/signature/template/list", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "模板列表", notes = "模板列表")
    public String templateList(@RequestParam Map<String, String> params) throws Exception {
        Instance instance = instanceService.findByInstanceIdAndType(params.get("instanceId"), InstanceTypeEnum.SIGNATURE.getCode());
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        return ValueUtil.toJson(signatureService.templateList(params));
    }

    @RequestMapping(value = "/scrt/signature/template/perView", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "合同预览", notes = "合同预览")
    public String templateView(@RequestParam String templateNo, @RequestParam String instanceId, HttpServletResponse response) throws Exception {
        Instance instance = instanceService.findByInstanceIdAndType(instanceId, InstanceTypeEnum.SIGNATURE.getCode());
//        Instance instance = instanceService.findByInstanceId(instanceId);
        if (instance == null) {
            ValueUtil.isError("功能尚未开通");
        }
        return signatureService.templateView(templateNo, response);
    }

}
