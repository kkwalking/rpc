import org.junit.Test;
import top.zzk.rpc.server.RpcServer;
import top.zzk.rpc.server.raw.SocketServer;

/**
 * @author zzk
 * @date 2022/2/6
 * description
 */
public class SocketServerTest {
    /* 
    BIO方式Server启动测试
     */
    @Test
    public void bootTest() {
        
        //初始化server并分配一个序列化器给它
        RpcServer server = new SocketServer("127.0.0.1",8888);
        server.startup();
    }
    
}
