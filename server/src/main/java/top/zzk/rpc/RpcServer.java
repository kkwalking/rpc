package top.zzk.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.common.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author zzk
 * @date 2021/11/28
 * description  rpc的提供者（服务端）
 */
public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;
    private static final int corePoolSize = 5;
    private static final int maxPoolSize = 50;
    private static final int keepAliveTime = 60;
    private static final int workQueueSize = 100;
    private RequestHandler requestHandler = new RequestHandler();

    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(workQueueSize);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, workQueue, threadFactory);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器已启动...");
            logger.info("服务器监听端口:{}", port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接，(ip:{}, port:{})", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时发生错误：", e);
        }
    }
}
