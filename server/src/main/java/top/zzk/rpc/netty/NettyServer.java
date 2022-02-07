package top.zzk.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author zzk
 * @date 2021/12/9
 * description 使用netty实现NIO方式通信的服务器
 */
@Slf4j
public class NettyServer implements RpcServer {

    private final String host;
    private final int port;
    private final ServiceProvider serviceProvider;

    private final ServiceRegistry registry;
    private Serializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERILIZER);
    }

    public NettyServer(String host, int port, int serializerCode) {
        this.host = host;
        this.port = port;
        serviceProvider = new ServiceProviderImpl();
        registry = new NacosServiceRegistry();
        serializer = Serializer.getByCode(serializerCode);
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        serviceProvider.addServiceProvider(service, serviceClass);
        registry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        
    }

    @Override
    public void start() {
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
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
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
