package top.zzk.rpc.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.exception.RpcException;
import top.zzk.rpc.loadbalancer.LoadBalancer;
import top.zzk.rpc.loadbalancer.RandomLoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author zzk
 * @date 2022/2/6
 * description
 */
@Slf4j
public class NacosServiceDiscovery extends AbstractDiscovery {
    
    private final LoadBalancer loadBalancer;
    private NamingService namingService;
    public NacosServiceDiscovery(LoadBalancer loadBalancer, String discoveryHost, int discoveryPort) {
        if(loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
        this.discoveryHost = discoveryHost;
        this.discoveryPort = discoveryPort;
        try {
            namingService = NamingFactory.createNamingService(discoveryHost+":" + discoveryPort);
        } catch (NacosException e) {
            e.printStackTrace();
        }

    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            if (instances.size() == 0) {
                log.error("找不到对应的服务：" + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生：", e);
        }
        return null;
    }
}
