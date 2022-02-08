package top.zzk.rpc.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.annotation.Service;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.api.HelloService;

/**
 * @author zzk
 * @date 2021/11/28
 * description HelloService的具体实现
 */
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    
    @Override
    public String hello(HelloObject object) {
        logger.info("接收信息(id:{})：{}",object.getId(),object.getMessage());
        return "reply for \"" + object.getMessage() + "\":welcome! ";
    }
}
