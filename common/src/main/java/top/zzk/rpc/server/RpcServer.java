package top.zzk.rpc.server;

import top.zzk.rpc.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/8
 * description  RPC服务器接口抽象
 */
public interface RpcServer {
    int DEFAULT_SERILIZER = Serializer.DEFAULT_SERIALIZER;
    void start();
    void config();
    void startup();
    <T> void publishService(T service, String serviceName);
}
