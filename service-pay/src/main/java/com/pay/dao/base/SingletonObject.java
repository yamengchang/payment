package com.pay.dao.base;

import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.fasterxml.jackson.core.JsonParser.*;
import com.fasterxml.jackson.databind.*;

import java.math.*;
import java.text.*;
import java.util.*;

/**
 * 该类用于提供一些类似的单例的，无状态的对象
 * @author 唐东峰
 */
public class SingletonObject {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final BigDecimal HUNDRED = new BigDecimal(100);

	static {
		//允许出现特殊字符和转义符
		OBJECT_MAPPER.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
		OBJECT_MAPPER.setSerializationInclusion(Include.NON_ABSENT);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		OBJECT_MAPPER.setDateFormat(simpleDateFormat);
	}

}