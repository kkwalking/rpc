package top.zzk.rpc.common.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;

import java.net.InetSocketAddress;

/**
 * @author zzk
 * @date 2022/2/6
 * description
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {
    
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            Instance instance = namingService.getAllInstances(serviceName).get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生：", e);
        }
        return null;
    }
}
