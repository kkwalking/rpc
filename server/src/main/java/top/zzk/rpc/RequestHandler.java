package top.zzk.rpc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcResponseCode;
import top.zzk.rpc.serviceprovider.ServiceProvider;
import top.zzk.rpc.serviceprovider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zzk
 * @date 2021/11/28
 * description 服务端请求处理器，对请求进行具体服务调用
 */
@Slf4j
public class RequestHandler {

    private static final ServiceProvider serviceProvider = new ServiceProviderImpl();
    

    public static Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeMethod(rpcRequest, service);
    }

    private static Object invokeMethod(RpcRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParams());
            log.info("服务:{} 成功调用方法:{}", request.getInterfaceName(), request.getMethodName());
        } catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD, request.getRequestId());
        } 
        return result;
    }
}
