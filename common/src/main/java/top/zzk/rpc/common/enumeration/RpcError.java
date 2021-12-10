package top.zzk.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zzk
 * @date 2021/11/28
 * description RPC错误消息定义
 */
@AllArgsConstructor
@Getter
public enum RpcError {
    SERVICE_INVOCATION_FAILURE("服务调用出现异常"),
    SERVICE_NOT_FOUND("找不到对应服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册服务未实现任何接口"),
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的（反）序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型")
    ;
    private final String errorMessage;
    
}
