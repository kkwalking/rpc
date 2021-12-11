package top.zzk.rpc.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.entity.RpcResponse;

/**
 * @author zzk
 * @date 2021/12/9
 * description netty客户端入站处理器，在这里可以实现对入站数据的操作
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接到服务器({})", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            log.info(String.format("客户端接收到消息: %s", msg));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"+msg.getRequestId());
            ctx.channel().attr(key).set(msg);
//            ctx.channel().close();
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("消息处理过程中出现错误：");
        cause.printStackTrace();
        ctx.close();
    }
}
