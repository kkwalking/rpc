package top.zzk.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.client.netty.NettyClient;
import top.zzk.rpc.client.raw.SocketClient;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author zzk
 * @date 2021/11/28
 * description RPC客户端动态代理
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法:{}#{}", method.getDeclaringClass().getCanonicalName(), method.getName());
        RpcRequest request = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);

        //返回整个响应
        Object result = null;
        if (client instanceof NettyClient) {
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(request);
            try {
                result = completableFuture.get().getData();
            } catch (ExecutionException e) {
                logger.error("方法调用请求失败", e);
                return null;
            }
        } else if (client instanceof SocketClient) {
            RpcResponse rpcResponse = (RpcResponse) client.sendRequest(request);
            result = rpcResponse.getData();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
}
