package com.pay.service.impl;

import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.signature.Base64;
import com.omv.database.biz.BaseServiceImpl;
import com.pay.bean.Channel;
import com.pay.bean.InstanceTypeEnum;
import com.pay.bean.PayFactory;
import com.pay.dao.InstanceDao;
import com.pay.dao.InstanceExpandUnionDao;
import com.pay.dao.InstanceExpandXYV1Dao;
import com.pay.dao.InstanceExpandXYV2Dao;
import com.pay.entity.*;
import com.pay.service.InstanceService;
import com.pay.service.PaymentChannelService;
import com.pay.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwj on 2018/9/30.
 */
@Service
public class InstanceServiceImpl extends BaseServiceImpl<Instance, String>
        implements InstanceService {
    @Autowired
    private InstanceDao instanceDao;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private PaymentChannelService paymentChannelService;
    @Autowired
    private InstanceExpandUnionDao instanceExpandUnionDao;
    @Autowired
    private InstanceExpandXYV1Dao instanceExpandXYV1Dao;
    @Autowired
    private InstanceExpandXYV2Dao instanceExpandXYV2Dao;


    @Override
    public Instance findByInstanceId(String instanceKey) {
        return instanceDao.findByInstanceId(instanceKey);
    }

    @Override
    public Instance findByInstanceIdAndType(String instanceKey,String type) {
        return instanceDao.findByInstanceIdAndType(instanceKey,type);
    }

    @Override
    @Transactional
    public void saveInstance(Instance instance) throws Exception {

        String type = instance.getType();
        PaymentInfo paymentInfo = instance.getPaymentInfo();
        paymentInfo.setInstanceId(instance.getInstanceId());
        paymentInfoService.save(paymentInfo);


        if (instance.getChannel().equals(Channel.Union.name()) && (type.equals(InstanceTypeEnum.CONSUME.name()) || type.equals(InstanceTypeEnum.CONVENIENT.name()))) {
            InstanceExpandUnion expandUnion = instance.getExpandUnion();
            InstanceExpandUnion dbExpandUnion = instanceExpandUnionDao.findByUnionMerId(expandUnion.getUnionMerId());
            if (dbExpandUnion != null) {
                ValueUtil.isError("相同商户号，无法创建同类型实例！");
            }
            expandUnion.setInstanceId(instance.getInstanceId());
            instanceExpandUnionDao.save(expandUnion);
        } else if (instance.getChannel().equals(Channel.XY.name()) && (type.equals(InstanceTypeEnum.CONSUME.name()) || type.equals(InstanceTypeEnum.CONVENIENT.name()))) {
            InstanceExpandXYV1 expandXYV1 = instance.getExpandXYV1();
            InstanceExpandXYV1 dbExpandXYV1 = instanceExpandXYV1Dao.findByMrchId(expandXYV1.getMrchId());
            if (dbExpandXYV1 != null) {
                ValueUtil.isError("相同商户号，无法创建同类型实例！");
            }
            expandXYV1.setInstanceId(instance.getInstanceId());
            instanceExpandXYV1Dao.save(expandXYV1);
        } else if (instance.getChannel().equals(Channel.XY_V2.name()) && (type.equals(InstanceTypeEnum.CONSUME.name()) || type.equals(InstanceTypeEnum.CONVENIENT.name()))) {
            InstanceExpandXYV2 expandXYV2 = instance.getExpandXYV2();
//            InstanceExpandXYV2 dbExpandXYV2 = instanceExpandXYV1Dao.findByMrchId(expandXYV1.getMrchId());
//            if(dbExpandXYV1!=null){
//                if (!instances.isEmpty()) {
//                    ValueUtil.isError("相同商户号，无法创建同类型实例！");
//                }
//            }

            expandXYV2.setInstanceId(instance.getInstanceId());
            instanceExpandXYV2Dao.save(expandXYV2);
        }
        save(instance);
    }

    @Override
    public void perfectedPayment(Instance instance) throws IOException {
        String instanceId = instance.getInstanceId();

        Instance dbInstance = findByInstanceId(instanceId);
        String channelStr = dbInstance.getChannel();
        Channel channel = Channel.getChannel(channelStr);
        if (channel.equals(Channel.XY_V2)) {

        } else if (channel.equals(Channel.Union)) {
            saveExpandUnion(instance);
        }


        List<PaymentChannel> paymentChannels = instance.getPaymentChannelList();
        if (null != paymentChannels && paymentChannels.size() > 0) {
            for (PaymentChannel paymentChannel : paymentChannels) {
                ValueUtil.verifyParams("proRate,channelSign,channelName",
                        paymentChannel.getProRate(), paymentChannel.getChannelSign(), paymentChannel.getChannelName());
                if (Channel.getChannel(dbInstance.getChannel()).equals(Channel.Union)) {//如果是银联
                    String unionRateType = paymentChannel.getUnionRateType();
                    ValueUtil.verifyParams("unionRateType,unionProdId", unionRateType, paymentChannel.getUnionProdId());
                    if (unionRateType.equals("02")) {
                        ValueUtil.verifyParams("fixed", paymentChannel.getFixed());
                    } else if (unionRateType.equals("04")) {
                        ValueUtil.verifyParams("proRate", paymentChannel.getProRate());
                    } else if (unionRateType.equals("05")) {
                        ValueUtil.verifyParams("fixed,proRate",
                                paymentChannel.getFixed(), paymentChannel.getProRate());
                    } else {
                        ValueUtil.isError("非法参数： unionRateType");
                    }
                }

                paymentChannel.setInstanceId(instanceId);
            }
            paymentChannelService.deleteByInstanceId(instanceId);
            paymentChannelService.save(paymentChannels);
        }

        PaymentInfo paymentInfo = instance.getPaymentInfo();
        if (null != paymentInfo) {
            String paymentInfoId = paymentInfo.getId();
            PaymentInfo dbPaymentInfo = paymentInfoService.findOne(paymentInfoId);
            if (null != dbPaymentInfo) {
                updateDbPaymentInfo(dbPaymentInfo, paymentInfo);
                paymentInfoService.save(dbPaymentInfo);
            } else {
                paymentInfo.setInstanceId(dbInstance.getInstanceId());
                paymentInfoService.save(paymentInfo);
            }
        }

        save(dbInstance);

    }

    private void saveExpandUnion(Instance instance) throws IOException {
        String instanceId = instance.getInstanceId();
        InstanceExpandUnion expandUnion = instance.getExpandUnion();
        MultipartFile acqPubFile = expandUnion.getAcqPubFile();
        MultipartFile acqPreFile = expandUnion.getAcqPreFile();
        MultipartFile signCertFile = expandUnion.getSignCertFile();
        MultipartFile middleCertFile = expandUnion.getMiddleCertFile();
        MultipartFile rootCertFile = expandUnion.getRootCertFile();
        InstanceExpandUnion dbExpandUnion = instanceExpandUnionDao.findByInstanceId(instanceId);
        if (dbExpandUnion == null) {
            expandUnion.setInstanceId(instanceId);
            if (signCertFile != null) {
                byte[] signCertBytes = signCertFile.getBytes();
                String signCertStr = Base64.encode(signCertBytes);
                expandUnion.setSignCert(signCertStr);
            }
            if (middleCertFile != null) {
                byte[] middleCertByte = middleCertFile.getBytes();
                String middleCertStr = Base64.encode(middleCertByte);
                expandUnion.setMiddleCert(middleCertStr);
            }
            if (rootCertFile != null) {
                byte[] rootCertBytes = rootCertFile.getBytes();
                String rootCertStr = Base64.encode(rootCertBytes);
                expandUnion.setRootCert(rootCertStr);
            }
            if (acqPubFile != null) {
                byte[] acqPubBytes = acqPubFile.getBytes();
                String acqPubKeyStr = new String(acqPubBytes, "UTF-8");
                expandUnion.setAcqPubKey(acqPubKeyStr);
            }
            if (acqPreFile != null) {
                byte[] acqPreBytes = acqPreFile.getBytes();
                String acqPreStr = new String(acqPreBytes, "UTF-8");
                expandUnion.setAcqPrvKey(acqPreStr);
            }
        } else {
            if (expandUnion.getUnionMerId() != null) {
                dbExpandUnion.setUnionMerId(expandUnion.getUnionMerId());
            }

            if (signCertFile != null) {
                byte[] signCertBytes = signCertFile.getBytes();
                String signCertStr = Base64.encode(signCertBytes);
                dbExpandUnion.setSignCert(signCertStr);
            }
            if (middleCertFile != null) {
                byte[] middleCertByte = middleCertFile.getBytes();
                String middleCertStr = Base64.encode(middleCertByte);
                dbExpandUnion.setMiddleCert(middleCertStr);
            }
            if (rootCertFile != null) {
                byte[] rootCertBytes = rootCertFile.getBytes();
                String rootCertStr = Base64.encode(rootCertBytes);
                dbExpandUnion.setRootCert(rootCertStr);
            }
            if (acqPubFile != null) {
                byte[] acqPubBytes = acqPubFile.getBytes();
                String acqPubKeyStr = new String(acqPubBytes, "UTF-8");
                dbExpandUnion.setAcqPubKey(acqPubKeyStr);
            }
            if (acqPreFile != null) {
                byte[] acqPreBytes = acqPreFile.getBytes();
                String acqPreStr = new String(acqPreBytes, "UTF-8");
                dbExpandUnion.setAcqPrvKey(acqPreStr);
            }
        }
    }

    private void updateDbPaymentInfo(PaymentInfo dbPaymentInfo, PaymentInfo paymentInfo) {
        dbPaymentInfo.setAlipayCallBackUrl(paymentInfo.getBankCallBackUrl());
        dbPaymentInfo.setWxCallBackUrl(paymentInfo.getWxCallBackUrl());
    }

    @Override
    public List<Instance> getAllXY() {
        return instanceDao.getAllXY();
    }

}
