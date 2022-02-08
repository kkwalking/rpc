import org.junit.Test;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.client.netty.NettyClient;
import top.zzk.rpc.client.RpcClientProxy;
import top.zzk.rpc.common.loadbalancer.LoadBalancer;
import top.zzk.rpc.common.loadbalancer.RoundRobinLoadBalancer;
import top.zzk.rpc.common.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/9
 * description  对netty客户端进行功能点测试
 */
public class NettyClientTest {
    @Test
    public void bootstrap() {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        System.out.println("-----------------------------------------");
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        System.out.println("-----------------------------------------");
        EchoService echoService = rpcClientProxy.getProxy(EchoService.class);
        res = echoService.echo("test echoService from netty client");
        System.out.println(res);
        System.out.println("-----------------------------------------");
    }
    @Test
    public void loadBalanceTest() {
        LoadBalancer RoundRobin = new RoundRobinLoadBalancer();
        RpcClient client = new NettyClient(RoundRobin);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        System.out.println("-----------------------------------------");
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        for(int i = 0; i < 20; i ++) {
            String res = helloService.hello(object);
            System.out.println(res);
        }
    }
}
