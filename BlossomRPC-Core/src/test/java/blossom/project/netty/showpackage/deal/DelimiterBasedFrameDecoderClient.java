package blossom.project.netty.showpackage.deal;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DelimiterBasedFrameDecoderClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                // 客户端接收到消息的处理逻辑
                            }
                        });
                    }
                });

            ChannelFuture f = b.connect("localhost", 8080).sync();
            for (int i = 0; i < 10; i++) {
                ByteBuf buffer = Unpooled.copiedBuffer(("Msg" + i + "\n").getBytes());
                f.channel().writeAndFlush(buffer);
            }
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
