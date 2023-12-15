package blossom.project.netty.rpc.client;

import blossom.project.netty.rpc.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message record=(Message) msg;
        log.info("the receive message id: {}",record);
        super.channelRead(ctx, msg);
    }
}
