import org.junit.Test;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;
import top.zzk.rpc.client.RpcClientProxy;

/**
 * @author zzk
 * @date 2021/11/28
 * description
 */
public class ClientTest {
    @Test
    public void clientBoot() {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 8888);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(1, "this is zzk");
        String res = helloService.hello(object);
        System.out.println(res);
    }
    
    
}
