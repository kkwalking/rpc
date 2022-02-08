package top.zzk.rpc.common.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author zzk
 * @date 2022/2/8
 * description 负载均衡接口
 */
public interface LoadBalancer {
    /**
     * 负载均衡选择方法
     * @param instances  实例列表
     * @return
     */
    Instance select(List<Instance> instances);
}
