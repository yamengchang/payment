package com.omv.common.util.signature;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwj on 2018/8/11.
 */
public class RsaUtils {
    protected static final String ALGORITHM = "RSA";
    protected static final String PUBLIC_KEY = "PUBLIC_KKY";
    protected static final String PRIVATE_KEY = "PRIVATE_KEY";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成秘钥对，公钥和私钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, Object> genKeyPair() throws NoSuchAlgorithmException {
        Map<String, Object> keyMap = new HashMap<String, Object>();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(1024); // 秘钥字节数
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        keyMap.put(PUBLIC_KEY, java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        keyMap.put(PRIVATE_KEY, java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        return keyMap;
    }

    public static Map<String, String> genKeyPairFromFile(String fileName, String password, String alias) throws NoSuchAlgorithmException {
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(fileName), password.toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] publicByte = publicKey.getEncoded();
        byte[] privateByte = privateKey.getEncoded();
        String publicKeyStr = java.util.Base64.getEncoder().encodeToString(publicByte);
        String privateKeyStr = java.util.Base64.getEncoder().encodeToString(privateByte);
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put(PUBLIC_KEY, publicKeyStr);
        keyMap.put(PRIVATE_KEY, privateKeyStr);
        return keyMap;
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws InvalidKeySpecException
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        // 得到公钥
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePublic(x509EncodedKeySpec);
        // 加密数据，分段加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        int inputLength = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        while (inputLength - offset > 0) {
            if (inputLength - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offset, inputLength - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥解密
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        // 得到私钥
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
        // 解密数据，分段解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        int inputLength = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        while (inputLength - offset > 0) {
            if (inputLength - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offset, inputLength - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 获取公钥
     *
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取私钥
     *
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    public static void main(String[] args) throws Exception {
//		Map<String, Object> keyMap = RsaUtils.genKeyPair();
//		String publicKey = RsaUtils.getPublicKey(keyMap);
//		String privateKey = RsaUtils.getPrivateKey(keyMap);
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCS3tyLB+gzLIGdO21Dem4JLp5TZGYWJUQl9vvUcMTbt0u4fjqlQp8musB4uc7EJ/DrBrR+HB2aoq7LknlNtCnfOH1WuMIXfQdfV1oiR0yi+ZCSjZQB+jRCO19NEWqMhUcBeZcx8em/UKXP6CmSINln6ttZGnbl12deOEQ+keoPIwIDAQAB";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJLe3IsH6DMsgZ07bUN6bgkunlNkZhYlRCX2+9RwxNu3S7h+OqVCnya6wHi5zsQn8OsGtH4cHZqirsuSeU20Kd84fVa4whd9B19XWiJHTKL5kJKNlAH6NEI7X00RaoyFRwF5lzHx6b9Qpc/oKZIg2Wfq21kaduXXZ144RD6R6g8jAgMBAAECgYAO8OMItb42boGlCCWeZrcI8hgjLaSA/juHjS+jNfGg1G28kALRSwy7uOXZojVZmSKWFjGIXr3YPFKB3R2//OMBbO5ep5Lu62iBLmXgdYwEXV1o0c1ZYkgDKF9tYN+Yv5nHB5bSZ4QnxpZ3LudvNNuUKqLFlDlHwWO6pKmr5cc7yQJBAPEbrse8d0u8cn5T0Xj+3WQ2kGm92ExyOn5sHLlnKPL4IKz3xbbLZWS3O8s1UhB4csPrcc2yEbc6hs5sHO1nFKUCQQCb8SCNUWeUpo8LhBMpIqd28vDaTDrRh5whwsdGbYkn2TPDc4i1lkHbGrTt5IqLh5XNKi79QHiiv5kAZBv7GCInAkA2LDcgD5tqO+QpuCF3oyQRMSVPbOVdf8jewOHPUntj5BZLZrxYruiQMY9QwCE5LCb1GECQq/LJDXBejvIM8T01AkASK/4kGaldXC9tIx3sfDpRlSvV9G4iPpBGKuF35onGF/x9OThkGLdh5fHRiwFOEyW0u8awAlRMetFEh2XvU7efAkA9CUF86C6OhlfAof15UienWPOtds2I4xr68Foh0xhZAf9sJoYIX1gKuCg87V1Sxsp2O/lfKWwaYpNlZncjzyYV";
        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey);
        // 公钥加密
        String sourceStr = "我是谁？";
        System.out.println("加密前：" + sourceStr);
        byte[] encryptStrByte = RsaUtils.encryptByPublicKey(sourceStr.getBytes(), publicKey);
        String encryptStr = Base64.encodeBase64String(encryptStrByte);
        System.out.println("加密后：" + encryptStr);
        System.out.println("长度：" + encryptStr.length());
        // 私钥解密
        byte[] decryptStrByte = RsaUtils.decryptByPrivateKey(Base64.decodeBase64(encryptStr), privateKey);
        String sourceStr_1 = new String(decryptStrByte);
        System.out.println("解密后：" + sourceStr_1);
        /**
         * 加密源数据通过getBytes()方法转换成字节数组进行加密
         * 解密后的字节数组通过new String()方法转换成源字符串
         * 明文显示加解密过程的字节数组，例如公私钥，用Base64转换成可见字符
         */
    }

}
