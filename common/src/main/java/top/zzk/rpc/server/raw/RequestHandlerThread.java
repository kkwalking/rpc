package top.zzk.rpc.server.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.server.RequestHandler;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.entity.RpcResponse;
import top.zzk.rpc.serializer.Serializer;
import top.zzk.rpc.utils.ObjectReader;
import top.zzk.rpc.utils.ObjectWriter;

import java.io.*;
import java.net.Socket;


/**
 * @author zzk
 * description Socket服务器处理线程，在这里对请求数据进行处理
 */

public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private final Socket socket;
    private final Serializer serializer;

    public RequestHandlerThread(Socket socket, Serializer serializer) {
        this.socket = socket;
        this.serializer = serializer;
    }


    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(in);
            logger.info("请求服务:{},请求方法{}", rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
            Object result = RequestHandler.handle(rpcRequest);
            ObjectWriter.writeObject(out,RpcResponse.success(result,rpcRequest.getRequestId()), serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
