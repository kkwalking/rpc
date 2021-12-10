package top.zzk.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.entity.PackageConfig;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.serializer.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zzk
 * @date 2021/12/10
 * description
 */
@Slf4j
public class ObjectWriter {
    public static void writeObject(OutputStream out, Object obj, Serializer serializer) {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        try {
            dataOutputStream.writeInt(PackageConfig.MAGIC_NUMBER);
            if(obj instanceof RpcRequest) {
                dataOutputStream.writeInt(PackageConfig.RequestType);
            } else if (obj instanceof RpcResponse) {
                dataOutputStream.writeInt(PackageConfig.ResponseType);
            }
            dataOutputStream.writeInt(serializer.getCode());
            byte[] bytes = serializer.serialize(obj);
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.write(bytes);
            dataOutputStream.flush();
        } catch (IOException e) {
            log.error("序列化时发生错误:", e);
            throw new RpcException(RpcError.ENCODING_ERROR);
        }
    }
}
