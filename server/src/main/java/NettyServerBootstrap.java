import top.zzk.rpc.server.RpcServer;
import top.zzk.rpc.server.annotation.ServiceScan;
import top.zzk.rpc.server.netty.NettyServer;

/**
 * @author zzk
 * @date 2021/12/9
 * description Netty服务端启动器
 */
@ServiceScan(value = "top.zzk.rpc.serviceImpl")
public class NettyServerBootstrap{
    public static void main(String[] args) {
        /*
            args[0]:port  (required)
         */
        if(args.length == 0) {
            System.err.println("A port number for listening is required");
            return;
        }
        int port = Integer.parseInt(args[0]);
        
        RpcServer server = new NettyServer("127.0.0.1", port);
        server.startup();
    }
}
