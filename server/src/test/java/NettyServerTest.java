import org.junit.Test;
import top.zzk.rpc.RpcServer;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.common.registry.DefaultServiceRegistry;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.common.serializer.ProtobufSerializer;
import top.zzk.rpc.netty.NettyServer;
import top.zzk.rpc.serviceImpl.EchoServiceImpl;
import top.zzk.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/12/9
 * description  Netty服务器测试类
 */
public class NettyServerTest {
    
    /* 
    启动测试
     */
    @Test
    public void bootstrap() {
        ServiceRegistry registry = new DefaultServiceRegistry();

        HelloService helloService = new HelloServiceImpl();
        registry.register(helloService);
        EchoService echoService = new EchoServiceImpl();
        registry.register(echoService);
        RpcServer server = new NettyServer(registry);
        server.setSerializer(new ProtobufSerializer());
        server.start(9999);
    }
}
