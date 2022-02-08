package top.zzk.rpc.client.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.factory.SingletonFactory;
import top.zzk.rpc.common.serializer.Serializer;

import java.net.InetSocketAddress;


/**
 * @author zzk
 * @date 2021/12/9
 * description netty客户端入站处理器，在这里可以实现对入站数据的操作
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private final UnprocessedRequest unprocessedRequest;
    
    public NettyClientHandler() {
        unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接到服务器({})", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            log.info(String.format("客户端接收到消息: %s", msg));
            unprocessedRequest.complete(msg);
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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包[{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.getChannel((InetSocketAddress)ctx.channel().remoteAddress(),
                        Serializer.getByCode(Serializer.DEFAULT_SERIALIZER));
                RpcRequest request = new RpcRequest();
                request.setHeartBeat(true);
                channel.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
