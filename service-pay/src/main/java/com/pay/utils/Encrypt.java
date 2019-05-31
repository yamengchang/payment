package com.pay.utils;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.*;

public class Encrypt {
	/**
	 * AES加密字符串
	 * @param content 需要被加密的字符串
	 * @param password 加密需要的密码
	 * @return 密文
	 */
	public static byte[] encrypt(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者

			kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
			// 128位的key生产者
			//加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行

			SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥

			byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
			// null。

			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥

			Cipher cipher = Cipher.getInstance("AES");// 创建密码器

			byte[] byteContent = content.getBytes("utf-8");

			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

			return cipher.doFinal(byteContent);

		}catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密AES加密过的字符串
	 * @param content AES加密过过的内容
	 * @param password 加密时的密码
	 * @return 明文
	 */
	public static byte[] decrypt(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
			byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
			return cipher.doFinal(content); // 明文

		}catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String content = "sddfdfdfg";
		String password = "123";
		System.out.println("加密之前：" + content);

		// 加密
		byte[] encrypt = Encrypt.encrypt(content, password);
		System.out.println("加密后的内容：" + new String(Objects.requireNonNull(encrypt)));

		// 解密
		byte[] decrypt = Encrypt.decrypt(encrypt, password);
		System.out.println("解密后的内容：" + new String(Objects.requireNonNull(decrypt)));
	}
}
