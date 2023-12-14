package blossom.project.netty.showpackage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;

/**
 * @author: ZhangBlossom
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 */
public class PackageServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        System.out.println("receive msg from client ：" + new String(data, "UTF-8"));
        //return data to the clinet
        ByteBuf out = Unpooled.copiedBuffer(("this is return data"+System.currentTimeMillis()).getBytes());
        ctx.writeAndFlush(out);
    }
}
