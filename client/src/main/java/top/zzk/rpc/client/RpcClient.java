package top.zzk.rpc.client;

import top.zzk.rpc.common.entity.RpcRequest;

/**
 * @author zzk
 * @date 2021/12/8
 * description   RPC客户端抽象
 */
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
