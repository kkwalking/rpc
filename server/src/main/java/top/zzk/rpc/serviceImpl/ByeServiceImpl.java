package top.zzk.rpc.serviceImpl;

import top.zzk.rpc.annotation.Service;
import top.zzk.rpc.api.ByeService;

/**
 * @author zzk
 * @date 2022/2/8
 * description
 */
@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
