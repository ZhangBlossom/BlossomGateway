package blossom.project.netty.rpc.server;

import blossom.project.netty.rpc.enums.ReqTypeEnum;
import blossom.project.netty.rpc.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message=(Message)msg;
        log.info("the receive data is :{}",message);
        message.setBody("this is Server Response Message");
        message.getHeader().setReqType(ReqTypeEnum.PUT.getCode());
        //写回数据
        ctx.writeAndFlush(message);
    }
}
