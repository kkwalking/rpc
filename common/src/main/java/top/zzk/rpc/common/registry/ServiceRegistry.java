package top.zzk.rpc.common.registry;

import java.net.InetSocketAddress;

/**
 * @author zzk
 * @date 2021/12/6
 * description 服务注册中心通用接口
 */
public interface ServiceRegistry {
    
    /*
     * @Description: 将服务注册进注册中心
     * 
     * @param serviceName: 服务名称
	 * @param inetSocketAddress: 提供该服务的地址
     * @return void
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /*
     * @Description: 根据服务查找服务，以地址形式提供
     * 
     * @param serviceName: 服务名称
     * @return java.net.InetSocketAddress 服务地址
     */
    InetSocketAddress lookupService(String serviceName);
}
