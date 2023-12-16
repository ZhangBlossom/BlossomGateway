package blossom.project.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyZeroCopyExample {
    public static void main(String[] args) {
        // 创建一个ByteBuf实例并写入一些数据
        ByteBuf buffer = Unpooled.buffer(10);
        buffer.writeBytes(new byte[]{1, 2, 3, 4, 5});

        // 使用slice创建一个共享同一数据的新ByteBuf实例
        ByteBuf slicedBuffer = buffer.slice(0, buffer.readableBytes());

        // 修改原始ByteBuf中的数据
        buffer.setByte(0, 9);

        // 打印两个ByteBuf的内容
        printBufferContent("Original Buffer", buffer);
        printBufferContent("Sliced Buffer", slicedBuffer);

        // 释放ByteBuf资源
        buffer.release();
        //slicedBuffer.release(); // 注意：由于数据是共享的，这里不需要再次释放
    }

    private static void printBufferContent(String message, ByteBuf buffer) {
        System.out.print(message + ": [");
        while (buffer.isReadable()) {
            System.out.print(buffer.readByte());
            if (buffer.isReadable()) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
