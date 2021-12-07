package top.zzk.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * description  rpc客户端
 */
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream((socket.getOutputStream()));
            out.writeObject(rpcRequest);
            logger.info("write request to socket({}:{})", host, port);
            out.flush();
            RpcResponse response = (RpcResponse) in.readObject();
            logger.info("write response from socket({}:{}), response:{}", host, port,response);
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
