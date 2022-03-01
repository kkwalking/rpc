package top.zzk.rpc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.zzk.rpc.enumeration.RpcResponseCode;

import java.io.Serializable;

/**
 * @author zzk
 * @date 2021/11/28
 * description  RPC响应封装包
 */
@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    /**
     * 响应对应的请求号
     */
    private String requestId;
    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;


    /*
    静态方法生成success响应数据包
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response=new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(RpcResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }
    /*
    静态方法生成fail响应数据包
     */
    public static <T> RpcResponse<T> fail(RpcResponseCode code, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
