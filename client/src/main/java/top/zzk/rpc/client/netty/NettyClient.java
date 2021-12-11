package top.zzk.rpc.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.common.codec.CommonDecoder;
import top.zzk.rpc.common.codec.CommonEncoder;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.serializer.HessianSerializer;
import top.zzk.rpc.common.serializer.JsonSerializer;
import top.zzk.rpc.common.serializer.KryoSerializer;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.common.utils.MessageChecker;

/**
 * @author zzk
 * @date 2021/12/8
 * description
 */
@Slf4j
public class NettyClient implements RpcClient {
    private String host;
    private int port;
    private static final Bootstrap bootstrap;
    private static final NioEventLoopGroup eventExecutors;
    private Serializer serializer;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    static {
        eventExecutors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);

    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(this.serializer == null) {
            log.error("序列化器未初始化");
            throw new RpcException(RpcError.SERIALIZER_UNDEFINED);
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new CommonDecoder())
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new NettyClientHandler());
            }
        });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener( future1 -> {
                    if (future1.isSuccess()) {
                        log.info("客户端成功发送消息：{}", rpcRequest.toString());
                    } else {
                        log.error("发送消息时发生错误:", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"+rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                MessageChecker.check(rpcRequest, rpcResponse);
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生：", e);

        } 
        return null;
    }

    @Override
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
