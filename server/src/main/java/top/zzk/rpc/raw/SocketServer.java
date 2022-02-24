package top.zzk.rpc.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.AbstractRpcServer;
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
public class SocketServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    
    private final ExecutorService threadPool;

    public SocketServer(String host, int port) {
        config();
        this.host = host;
        this.port = port;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry(registryHost, registryPort);
        this.serviceProvider = new ServiceProviderImpl();
    }

    public SocketServer(String host, int port, int serializerCode) {
        this(host, port);
        this.serializer = Serializer.getByCode(serializerCode);
    }

    @Override
    public void start() {

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器已启动...");
            logger.info("服务器监听端口:{}", port);
//            new ShutdownHook(serviceRegistry).addHootForClearAllServices();
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
}
