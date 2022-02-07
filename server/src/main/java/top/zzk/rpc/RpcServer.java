package top.zzk.rpc;

import top.zzk.rpc.common.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/8
 * description  RPC服务器接口抽象
 */
public interface RpcServer {
    int DEFAULT_SERILIZER = 0;
    void start();
    
    <T> void publishService(T service, Class<T> serviceClass);
}
