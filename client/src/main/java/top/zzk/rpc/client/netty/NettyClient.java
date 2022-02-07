package top.zzk.rpc.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.client.RpcClient;
import top.zzk.rpc.common.discovery.NacosServiceDiscovery;
import top.zzk.rpc.common.discovery.ServiceDiscovery;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.common.utils.MessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zzk
 * @date 2021/12/8
 * description
 */
@Slf4j
public class NettyClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;
    private final Serializer serializer;
    private final ServiceDiscovery discovery;

    public NettyClient() {
        this(DEFAULT_SERIALIZER);
    }
    public NettyClient(Integer serializer) {
        this.serializer = Serializer.getByCode(serializer);
        this.discovery = new NacosServiceDiscovery();
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);

    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (this.serializer == null) {
            log.error("序列化器未初始化");
            throw new RpcException(RpcError.SERIALIZER_UNDEFINED);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = discovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.getChannel(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                if (future1.isSuccess()) {
                    log.info("客户端成功发送消息：{}", rpcRequest.toString());
                } else {
                    log.error("发送消息时发生错误:", future1.cause());
                }
            });
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
            RpcResponse rpcResponse = channel.attr(key).get();
            MessageChecker.check(rpcRequest, rpcResponse);
            result.set(rpcResponse.getData());
        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生：", e);
            Thread.currentThread().interrupt();
        }
        return result.get();
    }

}
