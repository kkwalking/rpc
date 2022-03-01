package top.zzk.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.entity.PackageConfig;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.entity.RpcResponse;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.exception.RpcException;
import top.zzk.rpc.serializer.Serializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zzk
 * @date 2021/12/10
 * description
 */
@Slf4j
public class ObjectReader {
    public static Object readObject(InputStream in) {
        DataInputStream dataInputStream = new DataInputStream(in);
        try {
            int magic = dataInputStream.readInt();
            if (magic != PackageConfig.MAGIC_NUMBER) {
                log.error("不识别的协议包");
                throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
            }
            int packageType = dataInputStream.readInt();
            Class<?> packageClass;
            if (packageType == PackageConfig.RequestType) {
                packageClass = RpcRequest.class;
            } else if (packageType == PackageConfig.ResponseType) {
                packageClass = RpcResponse.class;
            } else {
                log.error("不识别的数据包类型:", packageType);
                throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
            }
            int serializerCode = dataInputStream.readInt();
            Serializer serializer = Serializer.getByCode(serializerCode);
            if (serializer == null) {
                log.error("不识别的反序列化器:{}", serializerCode);
                throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
            }
            int length = dataInputStream.readInt();
            byte[] bytes = new byte[length];
            dataInputStream.read(bytes);
            return serializer.deserialize(bytes, packageClass);
        } catch (IOException e) {
            log.error("解码协议包时发生错误:",e);
            throw new RpcException(RpcError.ENCODING_ERROR);
        }
    }
}
