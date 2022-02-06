package top.zzk.rpc.common.discovery;

import java.net.InetSocketAddress;

/**
 * @author zzk
 * @date 2022/2/6
 * description 服务发现接口
 */
public interface ServiceDiscovery {
    /**
     * 
     * @param serviceName 服务名称
     * @return 服务实体（地址）
     */
    InetSocketAddress lookupService(String serviceName);
}
