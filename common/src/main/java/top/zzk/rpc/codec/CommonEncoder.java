package top.zzk.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.zzk.rpc.entity.PackageConfig;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.serializer.Serializer;

/**
 * @author zzk
 * @date 2021/12/8
 * description  通用编码器 （出站）  
 *              包格式: 
 *                  magicCode :  4 bytes
 *                  packageType: 4 bytes
 *                  serializerCode:  4 bytes
 *                  length:      4 bytes
 *                  object:      bytes of the serialized object
 */

public class CommonEncoder extends MessageToByteEncoder {
    private final Serializer serializer;

    public CommonEncoder(Serializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(PackageConfig.MAGIC_NUMBER);
        if(msg instanceof RpcRequest) {
            out.writeInt(PackageConfig.RequestType);
        } else {
            out.writeInt(PackageConfig.ResponseType);
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
