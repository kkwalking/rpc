package top.zzk.rpc.common.hook;

import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.factory.ThreadPoolFactory;
import top.zzk.rpc.common.registry.NacosServiceRegistry;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.common.utils.NacosUtils;

/**
 * @author zzk
 * @date 2022/2/7
 * description
 */
@Slf4j
public class ShutdownHook {
    private ServiceRegistry registry;

    public ShutdownHook(ServiceRegistry registry) {
        this.registry = registry;
    }

    public void addHootForClearAllServices() {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            registry.clearRegistry();
            ThreadPoolFactory.shutdownAll();
        }));
    }
}
