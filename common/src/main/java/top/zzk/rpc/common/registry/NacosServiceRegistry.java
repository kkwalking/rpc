package top.zzk.rpc.common.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;


import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author zzk
 * @date 2021/12/17
 * description Nacos服务注册中心
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    //todo: nacos注册中心的地址目前固定写死在这里了，并且是单机启动，后期应该实现通过配置文件更改
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort(),"zzk-rpc");
        } catch (NacosException e) {
            log.error("注册服务时出错：", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> services = namingService.getAllInstances(serviceName);
            Instance instance = services.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时出错", e);
        }
        return null;
    }
}
