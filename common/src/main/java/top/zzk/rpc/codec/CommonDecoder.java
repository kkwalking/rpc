package top.zzk.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.entity.PackageConfig;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.entity.RpcResponse;
import top.zzk.rpc.enumeration.RpcError;
import top.zzk.rpc.exception.RpcException;
import top.zzk.rpc.serializer.Serializer;

import java.util.List;


/**
 * @author zzk
 * @date 2021/12/8
 * description  通用解码器（入站）
 *              自定义数据包还解决了TCP粘包拆包问题
 *              这里可以使用一个Message将RpcRequest和RpcResponse统一起来
 *                包格式:
 *                    magicCode :  4 bytes
 *                    packageType: 4 bytes
 *                    serializerCode:  4 bytes
 *                    length:      4 bytes
 *                    object:      bytes of the serialized object
 */
@Slf4j
public class CommonDecoder extends ReplayingDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if(magic != PackageConfig.MAGIC_NUMBER) {
            log.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageType = in.readInt();
        Class<?> packageClass;
        if(packageType == PackageConfig.RequestType) {
            packageClass = RpcRequest.class;
        } else if( packageType == PackageConfig.ResponseType) {
            packageClass = RpcResponse.class;
        } else {
            log.error("不识别的数据包类型: {}", packageType);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = in.readInt();
        Serializer serializer = Serializer.getByCode(serializerCode);
        if(serializer == null) {
            log.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
            byte[] objBytes = new byte[length];
            in.readBytes(objBytes);
            Object obj = serializer.deserialize(objBytes, packageClass);
            out.add(obj);
        }
        
    }
}
