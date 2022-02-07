package top.zzk.rpc.common.hook;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.factory.ThreadPoolFactory;
import top.zzk.rpc.common.utils.NacosUtils;

import java.util.concurrent.ExecutorService;

/**
 * @author zzk
 * @date 2022/2/7
 * description
 */
@Slf4j
public class ShutdownHook {
    private static final ShutdownHook shutdownHook = new ShutdownHook();
    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }
    public void addHootForClearAllServices() {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtils.clearRegistry();
            ThreadPoolFactory.shutdownAll();
        }));
    }
}
