package blossom.project.netty.rpc.client;

import blossom.project.netty.rpc.codec.MessageDecode;
import blossom.project.netty.rpc.codec.MessageEncode;
import blossom.project.netty.rpc.enums.ReqTypeEnum;
import blossom.project.netty.rpc.protocol.Header;
import blossom.project.netty.rpc.protocol.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;


public class Client {

    public static void main(String[] args) {
        EventLoopGroup worker=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*1024,
                                9,
                                4,
                                0,
                                0))
                        .addLast(new MessageEncode())
                        .addLast(new MessageDecode())
                        .addLast(new ClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8080)).sync();
            Channel channel=future.channel();
            for (int i = 0; i < 10; i++) {
                Message message=new Message();
                Header header = new Header();
                header.setReqId(System.currentTimeMillis());
                header.setReqType(ReqTypeEnum.PUT.getCode());
                message.setHeader(header);
                String body="this is request body :"+i;
                message.setBody(body);
                channel.writeAndFlush(message);
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
        }
    }
}
