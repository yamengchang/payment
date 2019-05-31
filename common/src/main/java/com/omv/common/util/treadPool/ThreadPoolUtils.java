package com.omv.common.util.treadPool;

import com.omv.common.util.properties.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zwj on 2018/7/30.
 */
public class ThreadPoolUtils {

    private static int threadPoolMax = Integer.valueOf(
            PropertiesUtil
                    .getString("threadPoolMax", "application.properties")
                    == null ? "5" :
                    PropertiesUtil
                            .getString("threadPoolMax", "application.properties"));

    /*创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
    这种类型的线程池特点是：
    工作线程的创建数量几乎没有限制(其实也有限制的,数目为Interger. MAX_VALUE), 这样可灵活的往线程池中添加线程。
    如果长时间没有往线程池中提交任务，即如果工作线程空闲了指定的时间(默认为1分钟)，则该工作线程将自动终止。
    终止后，如果你又提交了新的任务，则线程池重新创建一个工作线程。
    在使用CachedThreadPool时，一定要注意控制任务的数量，否则，由于大量线程同时运行，很有会造成系统瘫痪。*/
    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /*创建一个指定工作线程数量的线程池。
    每当提交一个任务就创建一个工作线程，如果工作线程数量达到线程池初始的最大数，则将提交的任务存入到池队列中。
    FixedThreadPool是一个典型且优秀的线程池，它具有线程池提高程序效率和节省创建线程时所耗的开销的优点。
    但是，在线程池空闲时，即线程池中没有可运行任务时，它不会释放工作线程，还会占用一定的系统资源*/
    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadPoolMax);

    /*
        创建一个单线程化的Executor，即只创建唯一的工作者线程来执行任务，它只会用唯一的工作线程来执行任务，
        保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
        如果这个线程异常结束，会有另一个取代它，保证顺序执行。
        单工作线程最大的特点是可保证顺序地执行各个任务，并且在任意给定的时间不会有多个线程是活动的。
    */
    private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    /*创建一个定长的线程池，而且支持定时的以及周期性的任务执行，支持定时及周期性任务执行。*/
    private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(threadPoolMax);

    public static void addCacheThread(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }

    public static void addFixedThread(Runnable runnable) {
        fixedThreadPool.execute(runnable);
    }

    public static void addSingleThread(Runnable runnable) {
        singleThreadExecutor.execute(runnable);
    }

    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/7/30 11:42
    *@Description :
    *@Params :  * @param delayTime 延迟时间
    * @param exeuteTimes 执行间隔
    */
    public static void addScheduledThread(Runnable runnable, Long delayTime, Long exeuteTimes) {
        if (delayTime == null) {
            scheduledThreadPool.schedule(runnable, 1, TimeUnit.SECONDS);
        } else if (delayTime != null && exeuteTimes == null) {
            scheduledThreadPool.schedule(runnable, delayTime, TimeUnit.SECONDS);
        }
        if (delayTime != null && exeuteTimes != null) {
            scheduledThreadPool.scheduleAtFixedRate(runnable, delayTime, exeuteTimes, TimeUnit.SECONDS);
        }

    }
}
