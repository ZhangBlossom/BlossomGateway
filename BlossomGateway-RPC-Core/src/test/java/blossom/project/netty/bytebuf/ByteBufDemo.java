package blossom.project.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

public class ByteBufDemo {
    public static void main(String[] args) {
        // 创建一个ByteBuf实例
        ByteBuf buffer = Unpooled.buffer(50);

        // 写入一些数据
        buffer.writeBytes("Hello, Netty!".getBytes(StandardCharsets.UTF_8));
        showByteBuf(buffer);

        // 读取一个字节
        buffer.readByte();
        showByteBuf(buffer);

        // 标记当前的readerIndex
        buffer.markReaderIndex();
        
        // 读取一些数据
        byte[] bytes = new byte[5];
        buffer.readBytes(bytes);
        showByteBuf(buffer);

        // 重置到标记的readerIndex
        buffer.resetReaderIndex();
        showByteBuf(buffer);

        // 写入更多数据
        buffer.writeBytes(" More data".getBytes(StandardCharsets.UTF_8));
        showByteBuf(buffer);

        // 标记当前的writerIndex
        buffer.markWriterIndex();

        // 再次写入数据
        buffer.writeBytes(" Even more data".getBytes(StandardCharsets.UTF_8));
        showByteBuf(buffer);

        // 重置到标记的writerIndex
        buffer.resetWriterIndex();
        showByteBuf(buffer);

        // 跳过几个字节
        buffer.skipBytes(5);
        showByteBuf(buffer);

        // 释放ByteBuf
        buffer.release();
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
