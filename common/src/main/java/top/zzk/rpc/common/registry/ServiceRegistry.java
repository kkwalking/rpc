package top.zzk.rpc.common.registry;

/**
 * @author zzk
 * @date 2021/12/6
 * description 服务注册中心接口
 */
public interface ServiceRegistry {
    
    /*
     * @Description 注册服务方法
     * @Param service 某一类型为T的服务
     * @return 
     **/
    <T> void register(T service);
    
    /*
     * @Description 获取服务方法
     * @Param serviceName：服务名
     * @return 
     **/
    Object getService(String serviceName);
}
