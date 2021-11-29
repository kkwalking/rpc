package top.zzk.rpc.client;

import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;

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
        }
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        RpcClientProxy proxy = new RpcClientProxy(ip, port);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(1, "this is zzk");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
