import org.junit.Test;
import org.junit.runner.RunWith;
import top.zzk.rpc.RpcServer;
import top.zzk.rpc.annotation.ServiceScan;
import top.zzk.rpc.api.EchoService;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.netty.NettyServer;
import top.zzk.rpc.netty.NettyServerBootstrap;
import top.zzk.rpc.serviceImpl.EchoServiceImpl;
import top.zzk.rpc.serviceImpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/12/9
 * description  Netty服务器测试类
 */
public class NettyServerTest {
    
    /* 
    Nio方式Server启动测试
     */
    @Test
    public void bootstrap() {
        /**
         * 目前暂未找到如何测试模拟启动类带@ServiceScan的方式
         */
        RpcServer server = new NettyServer("127.0.0.1", 8888);
        server.start();
    }
}
