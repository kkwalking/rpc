package top.zzk.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.exception.RpcException;


import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author zzk
 * @date 2021/12/17
 * description Nacos服务注册中心
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {
    private String host;
    private int port;
    private NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private InetSocketAddress address;

    public NacosServiceRegistry(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            namingService = NamingFactory.createNamingService(host+":" + port);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
            this.address = address;
            serviceNames.add(serviceName);
        } catch (NacosException e) {
            log.error("注册服务时出错：", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
    @Override
    public void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            for (String serviceName : serviceNames) {
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    log.error("注销服务{}失败", serviceName, e);
                }
            }
        }
    }

}
