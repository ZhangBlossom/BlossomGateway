package blossom.project.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;


/**
 * @author: ZhangBlossom
 * @date: 2023/12/13 21:29
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyTest类
 */
// NettyTest类定义
public class NettyTest {
    public static void main(String[] args) throws IOException {
        // 创建 boss 线程组用于处理服务器端接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建 worker 线程组用于进行 SocketChannel 的网络读写
        EventLoopGroup workGroup = new NioEventLoopGroup(2);

        // 创建 ServerBootstrap 对象，用于配置服务器的启动参数
        ServerBootstrap bootstrap = new ServerBootstrap();

        // 设置使用的 EventLoopGroup，设置用于创建服务器的 NioServerSocketChannel，
        // 并且设置 childHandler 来处理每一个连接的数据读写
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        // 初始化每个连接的管道，添加自定义的处理器
                        channel.pipeline()
                                .addLast(new NettyHttpServerHandler())
                                .addLast(new NettyServerConnectManagerHandler());
                    }
                });

        // 初始化 ChannelFuture，用于异步操作的通知回调
        ChannelFuture channelFuture = null;
        try {
            // 绑定服务器端口并启动服务器
            channelFuture = bootstrap.bind(8080).sync();
            System.out.println("Netty start successfully!");

            // 同步等待直到服务器端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // 异常处理
            throw new RuntimeException(e);
        } finally {
            // 优雅关闭 worker 和 boss 线程组
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
}