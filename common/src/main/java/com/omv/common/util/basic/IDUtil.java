package com.omv.common.util.basic;

import com.omv.common.util.error.CustomException;
import org.apache.commons.lang3.RandomUtils;

/**
 * Created by zwj on 2018/3/21.
 */
public class IDUtil {

    static {
        new IDUtil(getWorkerId());
    }

    /**
     * 开始时间截 (2017-04-14)
     */
    private static final long twepoch = 1492153224787L;

    /**
     * 机器id所占的位数
     */
    private static final long workerIdBits = 10L;

    /**
     * 支持的最大机器id，结果是1023 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 序列在id中占的位数
     */
    private static final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 时间截向左移22位(10+12)
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~1023)
     */
    private static long workerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private static long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private static long lastTimestamp = -1L;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param workerId 工作ID (0~1023)
     */
    public IDUtil(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized static String getID() throws CustomException {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            ValueUtil.isError("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID

        return String.valueOf(((timestamp - twepoch) << timestampLeftShift) //
                | (workerId << workerIdShift) //
                | sequence);
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected static long timeGen() {
        return System.currentTimeMillis();
    }

    protected static long getWorkerId() {
//        long workerId = IPAddress.
//        if (workerId == -1l) {
//            /**
//             * 本系统SnowflakeIdWorker的workerId范围为0-1023，ip最后一段数字最大为255
//             * 一旦获取本机ip失败，就取300-1023之间的随机数做为workerId
//             */
//            workerId = RandomUtils.nextLong(300, 1023);
//        }
        long workerId = RandomUtils.nextLong(300, 1023);
        return workerId;
    }
}
