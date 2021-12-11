package top.zzk.rpc.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.codec.CommonDecoder;
import top.zzk.rpc.common.codec.CommonEncoder;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.serializer.Serializer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zzk
 * @date 2021/12/11
 * description
 */
@Slf4j
public class ChannnelProvider {
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    public static Channel getChannel(InetSocketAddress inetSocketAddress, Serializer serializer) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("获取channel出错", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress socketAddress, CountDownLatch downLatch) {
        connect(bootstrap, socketAddress, MAX_RETRY_COUNT, downLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress socketAddress, int maxRetryCount, CountDownLatch downLatch) {
         bootstrap.connect(socketAddress).addListener((ChannelFutureListener) future -> {
             if (future.isSuccess()) {
                 log.info("客户端连接成功");
                 channel = future.channel();
                 downLatch.countDown();
                 return;
             }
             if (maxRetryCount == 0) {
                 log.error("客户端连接失败：已达到最大重试次数。");
                 downLatch.countDown();
                 throw new RpcException(RpcError.FAIL_CONNECT_SERVER);
             }
             //尝试重连，order:第i次重连
             int order = (MAX_RETRY_COUNT - maxRetryCount) + 1;
             //本次重连间隔
             int delay = 1 << order;
             log.error("{},连接失败，尝试重连:{}/{}", new Date(), order, MAX_RETRY_COUNT);
             bootstrap.config().group().schedule(
                     () -> connect(bootstrap, socketAddress, maxRetryCount-1, downLatch),
                     delay, TimeUnit.SECONDS);
         });
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接超时时间，超过该时间则连接失败, 5s
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //开启TCP底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启Nagle算法，关闭它。Nagle算法会尽可能地发送大数据块，从而导致小块数据在缓冲区停留
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
