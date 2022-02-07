package top.zzk.rpc.common.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zzk
 * @date 2021/12/10
 * description  线程池工厂，方便构造线程池
 */
@Slf4j
public class ThreadPoolFactory {
    /*
    线程池参数
     */
    public static final int CORE_POOL_SIZE = 10;
    public static final int MAX_POOL_SIZE = 100;
    public static final int KEEP_ALIVE_TIME = 1;
    public static final int BLOCKING_QUEUE_SIZE = 100;
    private static final Map<String, ExecutorService> threadPollMap = new ConcurrentHashMap<>();
    
    private ThreadPoolFactory() {}
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }
    
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        ExecutorService pool = threadPollMap.computeIfAbsent(threadNamePrefix, 
                k -> createThreadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPollMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPollMap.put(threadNamePrefix, pool);
        }
        return pool;
    }
    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
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
    public static void shutdownAll() {
        log.info("正在关闭所有线程池...");
        threadPollMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("关闭线程池[{}] [{}]", entry.getKey(), entry.getValue());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("关闭{}线程池失败！", entry.getKey());
                executorService.shutdownNow();
            }
        });
    }
}
