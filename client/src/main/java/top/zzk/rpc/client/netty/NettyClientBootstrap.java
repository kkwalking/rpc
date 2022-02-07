package top.zzk.rpc.client.netty;

import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.client.RpcClientProxy;
import top.zzk.rpc.common.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/9
 * description  netty客户端启动类
 */
public class NettyClientBootstrap {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
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
        System.exit(0);
    }
}
