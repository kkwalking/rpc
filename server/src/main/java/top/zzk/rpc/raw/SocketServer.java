package top.zzk.rpc.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.RpcServer;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.hook.ShutdownHook;
import top.zzk.rpc.common.registry.NacosServiceRegistry;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.common.factory.ThreadPoolFactory;
import top.zzk.rpc.serviceprovider.ServiceProvider;
import top.zzk.rpc.serviceprovider.ServiceProviderImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author zzk
 * @date 2021/11/28
 * description  Socket服务器
 */
public class SocketServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    
    private final ExecutorService threadPool;
    private Serializer serializer;
    private final String host;
    private final int port;
    private final ServiceProvider serviceProvider;
    private final ServiceRegistry registry;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERILIZER);
    }

    public SocketServer(String host, int port, int serializerCode) {
        this.host = host;
        this.port = port;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.registry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = Serializer.getByCode(serializerCode);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器已启动...");
            logger.info("服务器监听端口:{}", port);
            ShutdownHook.getShutdownHook().addHootForClearAllServices();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接，(ip:{}, port:{})", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时发生错误：", e);
        }
    }
    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        serviceProvider.addServiceProvider(service, serviceClass);
        registry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
    }
}
