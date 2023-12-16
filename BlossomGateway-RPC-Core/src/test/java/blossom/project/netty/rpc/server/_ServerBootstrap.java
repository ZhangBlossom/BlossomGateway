package blossom.project.netty.rpc.server;

import blossom.project.netty.rpc.codec.MessageDecode;
import blossom.project.netty.rpc.codec.MessageEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 21:41
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ServerBootstrap类
 */
@Slf4j
public class _ServerBootstrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        //TODO ServerBootstrap的创建可以考虑用工厂或者策略
        //因为这里可以用Epoll/Nio两种Channel
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    //构建处理客户端连接的ChannelPipeline
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(
                                        1024 * 1024,
                                        9,
                                        4,
                                        0,
                                        0))
                                //添加我们自己的编解码器以及处理器
                                .addLast(new MessageEncode())
                                .addLast(new MessageDecode())
                                .addLast(new ServerMessageHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            log.info("server startup on port {}", 8080);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException("There are some exceptions occurring " + "during the startup of the service, " +
                    "exceptions are : {} ", e);
        } finally {
            log.info("shutdown gracefully!!!");
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}
