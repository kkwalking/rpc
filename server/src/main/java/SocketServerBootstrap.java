import top.zzk.rpc.server.RpcServer;
import top.zzk.rpc.server.annotation.ServiceScan;
import top.zzk.rpc.server.raw.SocketServer;

/**
 * @author zzk
 * @date 2021/11/29
 * description socket服务端启动类
 */
@ServiceScan("top.zzk.rpc.serviceImpl")
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
        RpcServer server = new SocketServer("127.0.0.1",port);
        server.startup();
    }
}
