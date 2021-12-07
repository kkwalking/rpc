package top.zzk.rpc.client;

import top.zzk.rpc.api.EchoMessage;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;

import java.nio.Buffer;

/**
 * @author zzk
 * @date 2021/11/29
 * description
 */
public class RpcClientBootstrap {
    /*
        usage: RpcClientBootstrap <ip> <port>
     */
    public static void main(String[] args) {
        if(args.length !=2) {
            System.out.println("A Specific IP address and port is required");
            System.out.println("Usage: RpcClientBootstrap <IP> <Port>");
            return;
        }
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        RpcClientProxy proxy = new RpcClientProxy(ip, port);
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
