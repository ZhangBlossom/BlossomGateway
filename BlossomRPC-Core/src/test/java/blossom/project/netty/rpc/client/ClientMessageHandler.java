package blossom.project.netty.rpc.client;

import blossom.project.netty.rpc.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 21:41
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ClientHandler用于处理客户端接收到的消息
 */
@Slf4j
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        log.info("Client Receive Message is: {}", message);
        //这里直接调用父类的read方法
        super.channelRead(ctx, msg);
    }
}
