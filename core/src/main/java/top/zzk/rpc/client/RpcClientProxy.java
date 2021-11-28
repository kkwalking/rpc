package top.zzk.rpc.client;

import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zzk
 * @date 2021/11/28
 * description
 */
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient client = new RpcClient();
        return ((RpcResponse)client.sendRequest(request, host, port)).getData();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> claszz) {
        return (T) Proxy.newProxyInstance(claszz.getClassLoader(), new Class<?>[]{claszz}, this);
    }
}
