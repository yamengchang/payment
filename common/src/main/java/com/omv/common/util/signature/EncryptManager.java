package com.omv.common.util.signature;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.Base64Utils;

import java.util.Map;

public class EncryptManager {
    private String privateKey;
    private String publicKey;

    private String pingKey = null;
    private String workKey = null;
    private String mobKey = null;

    public EncryptManager() {
        init();
    }

    public EncryptManager(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        init();
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPingKey() {
        return pingKey;
    }

    public void setPingKey(String pingKey) {
        this.pingKey = pingKey;
    }

    public String getWorkKey() {
        return workKey;
    }

    public void setWorkKey(String workKey) {
        this.workKey = workKey;
    }

    public String getMobKey() {
        return mobKey;
    }

    public void setMobKey(String mobKey) {
        this.mobKey = mobKey;
    }

    private void assambleMobKey() {
        String mobKeyStr = getPingKey() + getWorkKey();
        String mob = null;
        try {
            mob = Base64Utils.encodeToString(
                    RsaUtils.encryptByPublicKey(mobKeyStr.getBytes(), publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setMobKey(mob);
        ;
    }

    private void init() {
        setPingKey(RandomStringUtils.random(24, "1234567890"));
        setWorkKey(RandomStringUtils.random(24, "1234567890"));
        assambleMobKey();
    }

    public void encryptMap(Map<String, String> dataMap) {
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String value = entry.getValue();
            entry.setValue(encryptStr(value));
        }
        SignUtils.getSign(dataMap, getPrivateKey());
        dataMap.put("mobKey", getMobKey());
    }

    public void decryptMap(Map<String, String> dataMap) {
        String mobKey = dataMap.get("mobKey");
        dataMap.remove("mobKey");
        parseMobKey(mobKey);
        SignUtils.verifySign(dataMap, getPublicKey());
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("mobKey")) {
                String value = entry.getValue();
                entry.setValue(decryptStr(value));
            }

        }
    }

    /**
     * 加密数据
     *
     * @param data
     * @return
     */
    public String encryptStr(String data) {
        if (data == null || "".equals(data.trim())) {
            return null;
        }
        try {
            return DesUtils.encryptByKey(data, getPingKey());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解密数据
     *
     * @param data
     * @return
     */
    public String decryptStr(String data) {
        if (data == null || "".equals(data.trim())) {
            return null;
        }
        try {
            return DesUtils.decryptByKey(data, getPingKey());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过mobKey获取pingKey和workKey
     *
     * @param mobKey
     */
    public void parseMobKey(String mobKey) {
        if (mobKey == null || "".equals(mobKey.trim())) {
            return;
        }
        try {
            byte[] mobKeyByte = Base64Utils.decodeFromString(mobKey);
            byte[] resultByte = RsaUtils.decryptByPrivateKey(mobKeyByte, privateKey);
            String result = new String(resultByte);
            String pingKey = result.substring(0, 24);
            String workKey = result.substring(24, 48);
            this.setPingKey(pingKey);
            this.setWorkKey(workKey);
            this.setMobKey(mobKey);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


}
