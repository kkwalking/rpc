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
    NULL_SERVICE("注册服务为空");
    
    private final String errorMessage;
    
}
