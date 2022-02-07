package top.zzk.rpc.raw;

import top.zzk.rpc.RpcServer;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.common.serializer.ProtobufSerializer;
import top.zzk.rpc.serviceImpl.EchoServiceImpl;
import top.zzk.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/11/29
 * description socket服务端启动类
 */
public class SocketServerBootstrap {
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
        //初始化server并分配一个序列化器给它
        RpcServer server = new SocketServer("127.0.0.1",port);
        server.publishService(helloService, HelloService.class);
        server.publishService(echoService, EchoService.class);
        server.start();
    }
}
