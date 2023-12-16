package blossom.project.netty.rpc.codec;

import blossom.project.netty.rpc.protocol.Header;
import blossom.project.netty.rpc.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

@Slf4j
public class MessageEncode extends MessageToByteEncoder<Message> {
    /**
     * msg就是我们要编码的数据，这里的数据就是Message类
     * @param ctx           the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg           the message to encode
     * @param out           the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        if (Objects.isNull(msg)){
            log.info("the Message is Null!!!");
            return;
        }
        Header header=msg.getHeader();
        out.writeLong(header.getReqId());
        out.writeByte(header.getReqType());
        //得到消息body 然后计算length
        Object body=msg.getBody();
        if(body!=null){
            //序列化得到byte数组
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(body);
            byte[] bytes= baos.toByteArray();
            //设定消息长度和消息体
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
            log.info("the final message is: {}",new String(bytes));
        }else{
            log.info("the message'length is zero!!!");
            out.writeInt(0);
        }
    }
}
