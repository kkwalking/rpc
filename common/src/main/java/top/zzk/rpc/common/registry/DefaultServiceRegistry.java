package top.zzk.rpc.common.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 默认服务注册中心
 **/
public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    
    private final Map<String, Object> registryMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    
    @Override
    public synchronized <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName))
            return;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0)
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        for (Class<?> i : interfaces) {
            registryMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口：{} 注册服务：{}", interfaces, serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object o = registryMap.get(serviceName);
        if (o == null)
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        return o;
    }
}
