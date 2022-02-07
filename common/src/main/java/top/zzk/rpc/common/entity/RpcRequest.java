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
@NoArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 请求号
     */
    private String requestId;

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
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
