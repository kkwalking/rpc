package top.zzk.rpc.client.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.common.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
//        logger.info("调用方法:{}#{}", method.getDeclaringClass().getCanonicalName(), method.getName());
        RpcRequest request = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());

        //返回整个响应
        return client.sendRequest(request);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
}
