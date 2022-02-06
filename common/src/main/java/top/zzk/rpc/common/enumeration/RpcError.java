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
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    SERIALIZER_UNDEFINED("序列化器未定义"),
    ENCODING_ERROR("编码协议包时发生错误"),
    DECODING_ERROR("解码协议包时发生错误"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    FAIL_CONNECT_SERVER("连接服务端失败"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败")
    ;
    private final String errorMessage;
    
}
