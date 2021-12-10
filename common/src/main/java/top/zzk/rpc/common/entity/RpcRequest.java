package top.zzk.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zzk
 * @date 2021/11/28
 * description RPC请求封装包
 */
@Data
@AllArgsConstructor
public class RpcRequest implements Serializable {

    public RpcRequest() {}

    /**
     * 待调用接口
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 调用参数值列表
     */
    private Object[] params;
    /* 调用参数类型列表*/
    private Class<?>[] paramTypes;
}
