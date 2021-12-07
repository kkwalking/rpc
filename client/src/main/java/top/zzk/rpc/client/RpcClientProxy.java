package top.zzk.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.common.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zzk
 * @date 2021/11/28
 * description
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final String host;
    private final int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        logger.info("调用方法:{}#{}", method.getDeclaringClass().getCanonicalName(), method.getName());
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient client = new RpcClient();
        //返回整个响应
        return client.sendRequest(request, host, port);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
}
