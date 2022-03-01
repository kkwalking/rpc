package top.zzk.rpc.client;

import top.zzk.rpc.entity.RpcRequest;

/**
 * @author zzk
 * @date 2021/12/8
 * description   RPC客户端抽象
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = 0;
    Object sendRequest(RpcRequest rpcRequest);
    void config();
    void setSerializer(int serializerCode);
}
