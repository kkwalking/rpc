package top.zzk.rpc.client;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.loadbalancer.LoadBalancer;
import top.zzk.rpc.loadbalancer.RandomLoadBalancer;
import top.zzk.rpc.serializer.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zzk
 * @date 2022/2/24 18:11
 * @desctiption
 */
@Slf4j
public abstract class AbstractRpcClient implements RpcClient{
    protected String discoveryHost;
    protected int discoveryPort;
    protected Serializer serializer;
    protected LoadBalancer loadBalancer;
    @Override
    public void config() {
        Properties pro = new Properties();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("client-conf.properties");
            pro.load(in);
            if (pro.get("loadbalance") != null) {
                String loadBalanceStr = pro.getProperty("loadbalance");
                switch (loadBalanceStr) {
                    default:
                    case "random":
                        loadBalancer = new RandomLoadBalancer();
                        break;
                    case "roundrobin":
                        loadBalancer = new RandomLoadBalancer();
                        break;
                }
                log.info("负载均衡策略为{}", loadBalanceStr);
            }
            if(pro.get("serializer") != null) {
                String serializerStr = pro.getProperty("serializer");
//                #可选[kryo, json, hessian, protobuf]
                switch (serializerStr){
                    default: //默认kryo
                    case "kryo":
                        this.serializer = new KryoSerializer();
                        break;
                    case "json":
                        this.serializer = new JsonSerializer();
                        break;
                    case "hessian":
                        this.serializer = new HessianSerializer();
                        break;
                    case "protobuf":
                        this.serializer = new ProtobufSerializer();
                        break;
                }
                log.info("序列化器为{}", serializerStr);
            } else {
                //默认为kryo
                this.serializer = new KryoSerializer();
            }
            this.discoveryHost = pro.getProperty("discovery_host", "127.0.0.1");
            log.info("服务发现ip为:{}", this.discoveryHost);
            this.discoveryPort = Integer.parseInt(pro.getProperty("discovery_port", "8848"));
            log.info("服务发现port为:{}", this.discoveryPort);

        } catch (FileNotFoundException e) {
            log.error("读取配置文件失败");
        } catch (IOException e) {
            log.error("配置文件加载失败");
        }
    }
}
