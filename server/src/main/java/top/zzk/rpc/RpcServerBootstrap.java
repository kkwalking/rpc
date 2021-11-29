package top.zzk.rpc;

import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.serviceimpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/11/29
 * description
 */
public class RpcServerBootstrap {
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
        RpcServer server = new RpcServer();
        server.register(helloService, port);
    }
}
