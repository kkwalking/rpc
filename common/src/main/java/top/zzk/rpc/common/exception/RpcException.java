package top.zzk.rpc.common.exception;

import top.zzk.rpc.common.enumeration.RpcError;

/**
 * @author zzk
 * @date 2021/11/28
 * description RPC调用异常
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcError error, String detail) {
        super(error.getErrorMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getErrorMessage());
    }
}
