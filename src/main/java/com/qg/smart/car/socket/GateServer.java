package com.qg.smart.car.socket;

import com.qg.smart.car.socket.handler.GateServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author hunger
 * @date 2017/8/3
 * Netty服务器配置类
 */
@Slf4j
public class GateServer {

    public GateServer(int port) {
        startGateServer(port);
        log.info("netty 服务器已启动");
    }

    /**
     * 配置服务器
     *
     * @param port 端口号
     */
    private static void startGateServer(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_LINGER, 0)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 心跳机制暂时使用TCP选项
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("DelimiterBasedFrameDecoder", new DelimiterBasedFrameDecoder(100 * 1024));
                            pipeline.addLast("ClientMessageHandler", new GateServerHandler());
                        }
                    });
            // 绑定端口
            bootstrap.bind(new InetSocketAddress(port)).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("[GateServer] Started Succeed, registry is complete, waiting for client connect...");
                } else {
                    log.error("[GateServer] Started Failed, registry is incomplete");
                }
            });
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
