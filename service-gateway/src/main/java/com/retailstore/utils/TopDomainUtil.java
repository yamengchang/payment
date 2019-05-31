package com.retailstore.utils;


import java.util.regex.*;

public class TopDomainUtil {

	private static final String RE_TOP = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co)\\b()*";

	private static Pattern pattern = Pattern.compile(RE_TOP, Pattern.CASE_INSENSITIVE);


	public static String getTopDomain(String url) {
		String result = url;
		try {
			Matcher matcher = pattern.matcher(url);
			matcher.find();
			result = matcher.group();
		}catch (Exception e) {
			System.out.println("[getTopDomain ERROR]====>");
			e.printStackTrace();
		}
		return result;
	}

	public static boolean check(String base, String sec) {
		return getTopDomain(base).equals(getTopDomain(sec));
	}
}
