package top.zzk.rpc.client.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.enumeration.RpcResponseCode;
import top.zzk.rpc.common.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author zzk
 * @date 2021/11/28
 * description  rpc客户端使用JDK原生socket
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    
    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket(this.host, this.port)) {
            ObjectOutputStream out = new ObjectOutputStream((socket.getOutputStream()));
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(rpcRequest);
            out.flush();
            RpcResponse response = (RpcResponse) in.readObject();
            if (response == null) {
                logger.error("服务调用失败,service:{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,
                        "service:" + rpcRequest.getInterfaceName());
            }
            if (response.getStatusCode() == null || response.getStatusCode() != RpcResponseCode.SUCCESS.getCode()) {
                logger.error("服务调用失败，service:{}, response:{}", rpcRequest.getInterfaceName(),
                        response);
            }
            return response.getData();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时发生错误：");
            throw new RpcException("服务调用失败：", e);
        }
    }
}
