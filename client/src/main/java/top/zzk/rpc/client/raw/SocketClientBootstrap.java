package top.zzk.rpc.client.raw;

import top.zzk.rpc.api.EchoMessage;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.client.RpcClientProxy;
import top.zzk.rpc.common.serializer.JsonSerializer;
import top.zzk.rpc.common.serializer.ProtobufSerializer;

/**
 * @author zzk
 * @date 2021/11/29
 * description  socket客户端启动类
 */
public class SocketClientBootstrap {
    /*
        usage: RpcClientBootstrap <ip> <port>
     */
    public static void main(String[] args) {

        //初始化一个客户端
        RpcClient socketClient = new SocketClient();
        
        RpcClientProxy proxy = new RpcClientProxy(socketClient);
        EchoService echoService = proxy.getProxy(EchoService.class);
        EchoMessage message = new EchoMessage(1, "this is zzk");
        String res = echoService.echo("hello zzk");
        System.out.println(res);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject helloMessage = new HelloObject(2, "hello message from zzk");
        res = helloService.hello(helloMessage);
        System.out.println(res);
    }
}
