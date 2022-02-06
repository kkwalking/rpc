package top.zzk.rpc;

import top.zzk.rpc.common.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/8
 * description  RPC服务器接口抽象
 */
public interface RpcServer {
    void start();
    void setSerializer(Serializer serializer);
    
    <T> void publishService(Object service, Class<T> serviceClass);
}
