import org.junit.Test;
import top.zzk.rpc.RpcServer;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
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
    Nio方式Server启动测试
     */
    @Test
    public void bootstrap() {

        HelloService helloService = new HelloServiceImpl();
        EchoService echoService = new EchoServiceImpl();
        RpcServer server = new NettyServer("127.0.0.1", 8888);
        server.setSerializer(new ProtobufSerializer());
        
        server.publishService(helloService, HelloService.class);
        server.publishService(echoService, EchoService.class);
        server.start();
    }
}
