package top.zzk.rpc.server.serviceprovider;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzk
 * @date 2021/12/17
 * description 默认的服务注册表，用来保存服务端本地服务,全局共享serviceMap和registeredService
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider{
    
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    
    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registeredService.contains(serviceName))
            return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("向接口:{} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
