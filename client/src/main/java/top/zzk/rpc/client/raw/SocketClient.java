package top.zzk.rpc.client.raw;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.common.discovery.NacosServiceDiscovery;
import top.zzk.rpc.common.discovery.ServiceDiscovery;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.common.utils.MessageChecker;
import top.zzk.rpc.common.utils.ObjectReader;
import top.zzk.rpc.common.utils.ObjectWriter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author zzk
 * @date 2021/11/28
 * description  rpc客户端使用JDK原生socket
 */
@Slf4j
public class SocketClient implements RpcClient {
    
    private Serializer serializer;
    
    private final ServiceDiscovery discovery;

    public SocketClient() {
        //todo 目前这里的服务发现是 Nacos, 并且是写死在这里的
        this.discovery = new NacosServiceDiscovery();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(this.serializer == null) {
            log.error("序列化器未初始化");
            throw new RpcException(RpcError.SERIALIZER_UNDEFINED);
        }
        InetSocketAddress inetSocketAddress = discovery.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            RpcResponse response = (RpcResponse) ObjectReader.readObject(inputStream);
            MessageChecker.check(rpcRequest,response);
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
