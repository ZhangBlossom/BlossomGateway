package blossom.project.netty.showpackage.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .handler(new ChannelInitializer<Channel>() {
                         @Override
                         protected void initChannel(Channel ch) {
                             ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                 @Override
                                 protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                     // 客户端接收到消息的处理
                                 }
                             });
                         }
                     });

            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
            for (int i = 0; i < 10; i++) {
                String message = "Message " + i;
                future.channel().writeAndFlush(Unpooled.copiedBuffer(message, StandardCharsets.UTF_8));
            }
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
