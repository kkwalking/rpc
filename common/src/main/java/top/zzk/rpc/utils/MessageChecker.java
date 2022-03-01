package top.zzk.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.entity.RpcResponse;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.enumeration.RpcResponseCode;
import top.zzk.rpc.exception.RpcException;

/**
 * @author zzk
 * @date 2021/12/11
 * description  检查响应与对应请求是否匹配
 */
@Slf4j
public class MessageChecker {
    private MessageChecker(){}
    
    public static void check(RpcRequest request, RpcResponse response) {
        if (response == null) {
            log.error("服务调用失败，service:{}", request.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "interface:" + request.getInterfaceName());
        }
        if (!request.getRequestId().equals(response.getRequestId())) {
            log.error("该响应与请求不匹配");
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, "requestId:" + request.getRequestId());
        }
        if (response.getStatusCode() == null || !response.getStatusCode().equals(RpcResponseCode.SUCCESS.getCode())) {
            log.error("服务调用失败，service:{}", request.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "interface:" + request.getInterfaceName());
        }
    }
}
