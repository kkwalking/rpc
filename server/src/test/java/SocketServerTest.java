import org.junit.Test;
import top.zzk.rpc.RpcServer;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.common.serializer.ProtobufSerializer;
import top.zzk.rpc.raw.SocketServer;
import top.zzk.rpc.serviceImpl.EchoServiceImpl;
import top.zzk.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2022/2/6
 * description
 */
public class SocketServerTest {
    /* 
    BIO方式Server启动测试
     */
    @Test
    public void bootTest() {
        
        HelloService helloService = new HelloServiceImpl();
        EchoService echoService = new EchoServiceImpl();
        //初始化server并分配一个序列化器给它
        RpcServer server = new SocketServer("127.0.0.1",8888);
        server.setSerializer(new ProtobufSerializer());
        server.publishService(helloService, HelloService.class);
        server.publishService(echoService, EchoService.class);
        server.start();
    }
    
}
