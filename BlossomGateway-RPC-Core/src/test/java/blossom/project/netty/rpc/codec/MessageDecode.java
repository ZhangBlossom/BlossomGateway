package blossom.project.netty.rpc.codec;

import blossom.project.netty.rpc.protocol.Header;
import blossom.project.netty.rpc.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MessageDecode extends ByteToMessageDecoder {

    /**
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (Objects.isNull(in)) {
            log.info("the ByteBuf is Null!!!");
            return;
        }
        Message message = new Message();
        //ByteBuf 表示接收到的消息报文
        Header header = new Header();
        header.setReqId(in.readLong());
        header.setReqType(in.readByte());
        header.setLength(in.readInt());
        message.setHeader(header);
        if (header.getLength() <= 0) {
            log.info("the body's length is zero!!!");
            return;
        }
        byte[] contents = new byte[header.getLength()];
        in.readBytes(contents);
        //反序列化得到数据内容
        ByteArrayInputStream bis = new ByteArrayInputStream(contents);
        ObjectInputStream ios = new ObjectInputStream(bis);
        message.setBody(ios.readObject());
        out.add(message);
        log.info("the final decoded message is:{}",message);
    }
}
