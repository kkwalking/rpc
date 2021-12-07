package top.zzk.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zzk
 * @date 2021/11/28
 * description
 */
@AllArgsConstructor
@Getter
public enum RpcError {
    SERVICE_INVOCATION_FAILURE("服务调用出现异常"),
    SERVICE_NOT_FOUND("找不到对应服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册服务未实现任何接口");
    
    private final String errorMessage;
    
}
