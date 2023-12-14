package blossom.project.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 15:27
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ByteBufZeroCopyDemo类
 */
public class ByteBufZeroCopyDemo {

    public static void main(String[] args) {
        // 创建一个ByteBuf并写入数据
        ByteBuf buffer = Unpooled.buffer(50);
        buffer.writeBytes("Hello, Netty!".getBytes(StandardCharsets.UTF_8));
        showByteBuf(buffer);
    }

    private static void showByteBuf(ByteBuf buf) {
        StringBuilder sb = new StringBuilder();
        sb.append("read index: ").append(buf.readerIndex());
        sb.append("\nwrite index: ").append(buf.writerIndex());
        sb.append("\ncapacity: ").append(buf.capacity()).append("\n");
        ByteBufUtil.appendPrettyHexDump(sb, buf);
        System.out.println(sb.toString());
    }
}
