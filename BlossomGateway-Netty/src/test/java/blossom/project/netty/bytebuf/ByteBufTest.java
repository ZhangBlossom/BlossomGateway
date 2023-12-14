package blossom.project.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 13:37
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ByteBufTest类
 */
public class ByteBufTest {

        public static void main(String[] args) {
            // 创建一个新的ByteBuf实例
            ByteBuf buffer = Unpooled.buffer(10);

            System.out.println("Initial capacity: " + buffer.capacity());

            // 写入一些数据到ByteBuf中
            String data = "hello";
            buffer.writeBytes(data.getBytes(StandardCharsets.UTF_8));

            // 打印写索引和读索引
            System.out.println("Write index after writing data: " + buffer.writerIndex());
            System.out.println("Read index after writing data: " + buffer.readerIndex());

            // 读取数据
            byte[] readData = new byte[buffer.readableBytes()];
            buffer.readBytes(readData);
            System.out.println("Data read from ByteBuf: " + new String(readData, StandardCharsets.UTF_8));

            // 再次打印写索引和读索引
            System.out.println("Write index after reading data: " + buffer.writerIndex());
            System.out.println("Read index after reading data: " + buffer.readerIndex());

            // 清除ByteBuf（重置读写索引）
            buffer.clear();
            System.out.println("Write index after clearing: " + buffer.writerIndex());
            System.out.println("Read index after clearing: " + buffer.readerIndex());

            // 释放ByteBuf
            buffer.release();
        }

}
