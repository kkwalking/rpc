import org.junit.Test;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.client.netty.NettyClient;
import top.zzk.rpc.client.RpcClientProxy;
import top.zzk.rpc.common.serializer.ProtobufSerializer;
import top.zzk.rpc.common.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/9
 * description  对netty客户端进行功能点测试
 */
public class NettyClientTest {
    @Test
    public void bootstrap() {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        client.setSerializer(Serializer.getByCode(Serializer.PROTOBUF));
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        System.out.println("-----------------------------------------");
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        System.out.println("-----------------------------------------");
        EchoService echoService = rpcClientProxy.getProxy(EchoService.class);
        res = echoService.echo("test echoService by netty way");
        System.out.println(res);
        System.out.println("-----------------------------------------");
    }
}
