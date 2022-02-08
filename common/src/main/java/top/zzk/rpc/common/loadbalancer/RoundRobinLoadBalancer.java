package top.zzk.rpc.common.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author zzk
 * @date 2022/2/8
 * description 轮询负载均衡
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    
    private int index = 0;
    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index = 0;
        }
        return instances.get(index++);
    }
}
