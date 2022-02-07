package top.zzk.rpc.common.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.utils.NacosUtils;


import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author zzk
 * @date 2021/12/17
 * description Nacos服务注册中心
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            NacosUtils.registerService(serviceName, address);
        } catch (NacosException e) {
            log.error("注册服务时出错：", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
