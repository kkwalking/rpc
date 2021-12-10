package top.zzk.rpc.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.RequestHandler;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * @author zzk
 * description Socket服务器处理线程，在这里对请求数据进行处理
 */

public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;

    public RequestHandlerThread(Socket socket,ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
    }


    @Override
    public void run() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())
             ) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            logger.info("请求服务:{},请求方法{}", rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = RequestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
