package top.zzk.rpc.common.entity;

import lombok.Data;
import top.zzk.rpc.common.enumeration.RpcResponseCode;

import java.io.Serializable;

/**
 * @author zzk
 * @date 2021/11/28
 * description  RPC响应封装包
 */
@Data
public class RpcResponse<T> implements Serializable {
    

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
    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response=new RpcResponse<>();
        response.setStatusCode(RpcResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }
    /*
    静态方法生成fail响应数据包
     */
    public static <T> RpcResponse<T> fail(RpcResponseCode code) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
