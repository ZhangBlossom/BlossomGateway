package blossom.project.netty.rpc;

import blossom.project.netty.rpc.codec.MessageDecode;
import blossom.project.netty.rpc.codec.MessageEncode;

import blossom.project.netty.rpc.enums.ReqTypeEnum;
import blossom.project.netty.rpc.protocol.Header;
import blossom.project.netty.rpc.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 21:07
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Bootstrap类
 */
@Slf4j
public class Bootstrap {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder
                        (1024,
                                9,
                                4,
                                0,
                                0),
                new LoggingHandler(),
                new MessageEncode(),
                new MessageDecode()
        );

        Header header = new Header();
        header.setReqId(5201314L);
        header.setReqType(ReqTypeEnum.GET.getCode());
        //这里不需要设定消息的长度，因为消息的长度我们在Encode里面设置了
        Message message = new Message();
        message.setHeader(header);
        message.setBody("I'll use Netty to write a RPC framework");

        //输出结果 这里会执行编码（因为我们是输出）
        //channel.writeOutbound(message);

        //申请空间
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        //进行手动的编码
        new MessageEncode().encode(null, message, buf);

        //对编码的内容进行解码
        //这里就会执行我们的Decode方法
        channel.writeInbound(buf);
    }
}
