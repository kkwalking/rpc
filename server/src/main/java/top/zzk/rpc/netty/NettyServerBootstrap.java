package top.zzk.rpc.netty;

import top.zzk.rpc.RpcServer;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.common.registry.DefaultServiceRegistry;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.serviceImpl.EchoServiceImpl;
import top.zzk.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/12/9
 * description
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
        ServiceRegistry registry = new DefaultServiceRegistry();

        HelloService helloService = new HelloServiceImpl();
        registry.register(helloService);
        EchoService echoService = new EchoServiceImpl();
        registry.register(echoService);
        
        RpcServer server = new NettyServer(registry);
        server.start(port);
    }
}
