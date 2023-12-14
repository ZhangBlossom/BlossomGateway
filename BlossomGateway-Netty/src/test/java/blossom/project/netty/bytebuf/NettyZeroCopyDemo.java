package blossom.project.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

public class NettyZeroCopyDemo {
    public static void main(String[] args) {
        // 创建两个ByteBuf实例
        ByteBuf header = Unpooled.copiedBuffer("Header", StandardCharsets.UTF_8);
        ByteBuf body = Unpooled.copiedBuffer("Body", StandardCharsets.UTF_8);

        // 创建一个CompositeByteBuf来组合这两个ByteBuf
        CompositeByteBuf message = Unpooled.compositeBuffer();
        message.addComponents(true, header, body);

        // 遍历CompositeByteBuf并打印内容
        for (ByteBuf buf : message) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
        }

        // 释放资源
        message.release(); // 由于在添加时设置了自动增加引用计数，这里只需释放CompositeByteBuf
    }
}
