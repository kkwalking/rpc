package top.zzk.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.AbstractRpcServer;
import top.zzk.rpc.RpcServer;
import top.zzk.rpc.common.codec.CommonDecoder;
import top.zzk.rpc.common.codec.CommonEncoder;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.hook.ShutdownHook;
import top.zzk.rpc.common.registry.NacosServiceRegistry;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.common.serializer.Serializer;
import top.zzk.rpc.serviceprovider.ServiceProvider;
import top.zzk.rpc.serviceprovider.ServiceProviderImpl;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zzk
 * @date 2021/12/9
 * description 使用netty实现NIO方式通信的服务器
 */
@Slf4j
public class NettyServer extends AbstractRpcServer {
    
    private Serializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERILIZER);
    }

    public NettyServer(String host, int port, int serializerCode) {
        this.host = host;
        this.port = port;
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();
        this.serializer = Serializer.getByCode(serializerCode);
    }
    

    @Override
    public void start() {
        scanServices();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            ShutdownHook.getShutdownHook().addHootForClearAllServices();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务器时发生错误:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
