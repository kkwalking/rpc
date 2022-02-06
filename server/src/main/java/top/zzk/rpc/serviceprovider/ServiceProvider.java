package top.zzk.rpc.serviceprovider;

/**
 * @author zzk
 * @date 2021/12/17
 * description 保存和提供服务实例
 */
public interface ServiceProvider {
    /*
     * @Description: 添加服务实例
     * 
     * @param service: 待添加的服务实例
     * @return void
     */
    <T> void addServiceProvider(T service);
    /*
     * @Description: 获取服务实例
     * 
     * @param serviceName: 想要获取的服务实例的服务名称
     * @return java.lang.Object
     */
    Object getServiceProvider(String serviceName);
}
