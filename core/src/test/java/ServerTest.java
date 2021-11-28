import org.junit.Test;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.server.RpcServer;
import top.zzk.rpc.serviceimpl.HelloServiceImpl;

/**
 * @author zzk
 * @date 2021/11/28
 * description
 */
public class ServerTest {
    @Test
    public void serverBoot() {
        HelloService helloService = new HelloServiceImpl();
        RpcServer server = new RpcServer();
        server.register(helloService, 8888);
    }
}
