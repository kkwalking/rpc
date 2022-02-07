package top.zzk.rpc.netty;

import top.zzk.rpc.RpcServer;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.serviceImpl.EchoServiceImpl;
import top.zzk.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/12/9
 * description Netty服务端启动器
 */
public class NettyServerBootstrap {
    public static void main(String[] args) {
        /*
            args[0]:port  (required)
         */
        if(args.length == 0) {
            System.err.println("A port number for listening is required");
            return;
        }
        int port = Integer.parseInt(args[0]);

        HelloService helloService = new HelloServiceImpl();
        EchoService echoService = new EchoServiceImpl();
        
        RpcServer server = new NettyServer("127.0.0.1", port);
        server.publishService(helloService, HelloService.class);
        server.publishService(echoService, EchoService.class);
        server.start();
    }
}
