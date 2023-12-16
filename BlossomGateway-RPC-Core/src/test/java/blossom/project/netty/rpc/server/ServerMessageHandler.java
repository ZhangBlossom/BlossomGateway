package blossom.project.netty.rpc.server;

import blossom.project.netty.rpc.enums.ReqTypeEnum;
import blossom.project.netty.rpc.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 21:41
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ServerHandler类
 * 服务端处理类
 * 读取数据并且进行处理
 */
@Slf4j
public class ServerMessageHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx
     * @param msg  这里就是我在解码器中out中设定的对象，可以是多个
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (Objects.isNull(message)){
            log.info("the Message is Null!!!");
            return ;
        }
        log.info("Server Receive Message : {}" , message);
        message.setBody("This is Server' response Message");
        message.getHeader().setReqType(ReqTypeEnum.GET.getCode());
        //将消息写回客户端
        ctx.writeAndFlush(message);
    }
}
