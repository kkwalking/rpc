package top.zzk.rpc.client.netty;

import top.zzk.rpc.entity.RpcResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzk
 * @date 2022/2/7
 * description
 */
public class UnprocessedRequest {
    
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> 
            unprocessedResponseFutures = new ConcurrentHashMap<>();
    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }
    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
