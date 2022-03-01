package top.zzk.rpc.hook;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.factory.ThreadPoolFactory;
import top.zzk.rpc.registry.ServiceRegistry;

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
