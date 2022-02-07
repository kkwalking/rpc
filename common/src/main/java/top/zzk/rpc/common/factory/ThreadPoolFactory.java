package top.zzk.rpc.common.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author zzk
 * @date 2021/12/10
 * description  线程池工厂，方便构造线程池
 */
public class ThreadPoolFactory {
    /*
    线程池参数
     */
    public static final int CORE_POOL_SIZE = 10;
    public static final int MAX_POOL_SIZE = 100;
    public static final int KEEP_ALIVE_TIME = 1;
    public static final int BLOCKING_QUEUE_SIZE = 100;
    
    private ThreadPoolFactory() {}
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }
    
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MINUTES,  workQueue, threadFactory);
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if(threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
