package top.zzk.rpc.client.raw;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.client.AbstractRpcClient;
import top.zzk.rpc.discovery.NacosServiceDiscovery;
import top.zzk.rpc.discovery.ServiceDiscovery;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.entity.RpcResponse;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.exception.RpcException;
import top.zzk.rpc.loadbalancer.LoadBalancer;
import top.zzk.rpc.serializer.Serializer;
import top.zzk.rpc.utils.MessageChecker;
import top.zzk.rpc.utils.ObjectReader;
import top.zzk.rpc.utils.ObjectWriter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author zzk
 * @date 2021/11/28
 * description  rpc客户端使用JDK原生socket
 */
@Slf4j
public class SocketClient extends AbstractRpcClient {

    private  ServiceDiscovery discovery;
    private Socket socket;

    public SocketClient() {
        config();
        this.discovery = new NacosServiceDiscovery(loadBalancer, discoveryHost, discoveryPort);
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void setSerializer(int serializerCode) {
        this.serializer = Serializer.getByCode(serializerCode);
    }

    @Override
    public void shutdown() {
        log.info("socket client shutdown now");
        try {
            socket.close();
        } catch (IOException e) {
            log.error("socket client shutdown error");
            System.exit(1);
        }
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(this.serializer == null) {
            log.error("序列化器未初始化");
            throw new RpcException(RpcError.SERIALIZER_UNDEFINED);
        }
        InetSocketAddress inetSocketAddress = discovery.lookupService(rpcRequest.getInterfaceName());
        try  {
            this.socket = new Socket();
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            RpcResponse response = (RpcResponse) ObjectReader.readObject(inputStream);
            MessageChecker.check(rpcRequest,response);
            return response;
        } catch (IOException e) {
            log.error("调用时发生错误：");
            throw new RpcException("服务调用失败：", e);
        }
    }

}
