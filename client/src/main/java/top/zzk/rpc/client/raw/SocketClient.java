package top.zzk.rpc.client.raw;

import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.enumeration.RpcResponseCode;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.common.utils.ObjectReader;
import top.zzk.rpc.common.utils.ObjectWriter;

import java.io.*;
import java.net.Socket;

/**
 * @author zzk
 * @date 2021/11/28
 * description  rpc客户端使用JDK原生socket
 */
@Slf4j
public class SocketClient implements RpcClient {
    
    private final String host;
    private final int port;
    private Serializer serializer;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(this.serializer == null) {
            log.error("序列化器未初始化");
            throw new RpcException(RpcError.SERIALIZER_UNDEFINED);
        }
        try (Socket socket = new Socket(this.host, this.port)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            RpcResponse response = (RpcResponse) ObjectReader.readObject(inputStream); 
            if (response == null) {
                log.error("服务调用失败,service:{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,
                        "service:" + rpcRequest.getInterfaceName());
            }
            if (response.getStatusCode() == null || response.getStatusCode() != RpcResponseCode.SUCCESS.getCode()) {
                log.error("服务调用失败，service:{}, response:{}", rpcRequest.getInterfaceName(),
                        response);
            }
            return response.getData();
        } catch (IOException e) {
            log.error("调用时发生错误：");
            throw new RpcException("服务调用失败：", e);
        }
    }

    @Override
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
