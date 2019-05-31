package com.omv.common.util.basic;

import java.io.*;

/**
 * Created by by on 2017/12/21.
 */
public class SerializeUtil {
    /**
     * 序列化对象
     * ＠throws IOException
     */
    public static byte[] serialize(Object object) {
        ByteArrayOutputStream saos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(saos);
            oos.writeObject(object);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saos.toByteArray();

    }

    /**
     * 反序列化对象
     * ＠throws IOException
     * ＠throws ClassNotFoundException
     */
    public static Object deserialize(byte[] buf) {
        Object object = null;
        ByteArrayInputStream sais = new ByteArrayInputStream(buf);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(sais);
            object = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
