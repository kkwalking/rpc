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
import top.zzk.rpc.common.factory.SingletonFactory;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.common.utils.MessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
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
    private final UnprocessedRequest unprocessedRequest;

    public NettyClient() {
        this(DEFAULT_SERIALIZER);
    }
    public NettyClient(Integer serializer) {
        this.serializer = Serializer.getByCode(serializer);
        this.discovery = new NacosServiceDiscovery();
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = discovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.getChannel(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequest.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future1 -> {
                if (future1.isSuccess()) {
                    log.info("客户端成功发送消息：{}", rpcRequest.toString());
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    log.error("发送消息时发生错误:", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequest.remove(rpcRequest.getRequestId());
            log.error("发送消息时有错误发生：", e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
