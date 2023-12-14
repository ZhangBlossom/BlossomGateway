package blossom.project.netty.showpackage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

/**
 * @author: ZhangBlossom
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 */
public class PackageClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client link successfully！！！");
        //发送消息
        for (int i = 0; i < 10; i++) {
            ByteBuf buf = Unpooled.copiedBuffer("client message :" + i, Charset.forName("utf-8"));
            ctx.writeAndFlush(buf);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("receive the msg from Server！！！");
        ByteBuf buf = (ByteBuf) msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        System.out.println("the Server msg is :" + new String(data, Charset.forName("utf-8")));
        super.channelRead(ctx, msg);
    }
}
