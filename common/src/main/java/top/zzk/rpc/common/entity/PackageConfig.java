package top.zzk.rpc.common.entity;

/**
 * @author zzk
 * @date 2021/12/9
 * description  RPC通信数据包的格式配置
 */
public interface PackageConfig {
    /*
    魔术字校验
     */
    int MAGIC_NUMBER = 0x0130e082;
    /*
     请求类型数据包
     */
    int RequestType = 0;
    /*
     响应类型数据包
     */
    int ResponseType = 1;
}
