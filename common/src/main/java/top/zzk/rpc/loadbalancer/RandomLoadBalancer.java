package top.zzk.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author zzk
 * @date 2022/2/8
 * description 随机负载均衡
 */
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        int index = new Random().nextInt(instances.size());
        return instances.get(index);
    }
}
