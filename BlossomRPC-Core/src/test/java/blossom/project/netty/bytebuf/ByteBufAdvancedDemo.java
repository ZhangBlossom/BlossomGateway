package blossom.project.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

public class ByteBufAdvancedDemo {
    public static void main(String[] args) {
        // 创建一个ByteBuf并写入数据
        ByteBuf buffer = Unpooled.buffer(50);
        buffer.writeBytes("Hello, Netty!".getBytes(StandardCharsets.UTF_8));
        showByteBuf(buffer);

        // 使用slice()创建一个新的ByteBuf，共享相同的数据
        ByteBuf slicedBuffer = buffer.slice(0, 5);
        showByteBuf(slicedBuffer);

        // 使用duplicate()复制整个ByteBuf，共享数据
        ByteBuf duplicatedBuffer = buffer.duplicate();
        showByteBuf(duplicatedBuffer);

        // 使用copy()创建ByteBuf的副本，数据不共享
        ByteBuf copiedBuffer = buffer.copy();
        showByteBuf(copiedBuffer);

        // 检查当前容量并扩展ByteBuf
        System.out.println("Capacity before ensuring writable: " + buffer.capacity());
        buffer.ensureWritable(100);
        System.out.println("Capacity after ensuring writable: " + buffer.capacity());

        // 释放ByteBuf资源
        buffer.release();
        slicedBuffer.release();
        duplicatedBuffer.release();
        copiedBuffer.release();
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
