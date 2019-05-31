package com.omv.common.util.basic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by zwj on 2018/3/26.
 */
public class StringUtil {

    public static InputStream str2Is(String content) {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        return is;
    }
}
