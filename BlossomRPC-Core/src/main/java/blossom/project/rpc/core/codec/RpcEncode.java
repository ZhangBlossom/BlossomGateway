package blossom.project.rpc.core.codec;

import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcHeader;
import blossom.project.rpc.core.serialize.Serializer;
import blossom.project.rpc.core.serialize.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 19:35
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcEncode类
 */
@Slf4j
public class RpcEncode extends MessageToByteEncoder<RpcDto<Object>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcDto<Object> msg, ByteBuf out) throws Exception {
        log.info("Start encoding the data");
        //判断请求头合法性
        if (Objects.isNull(msg)) {
            log.warn("the RpcDto msg is Null!!!");
            return;
        }
        RpcHeader RpcHeader=msg.getHeader();
        out.writeByte(RpcHeader.getVersionId());
        out.writeByte(RpcHeader.getAlgorithmType());
        out.writeByte(RpcHeader.getReqType());
        out.writeLong(RpcHeader.getReqId());
        //设定序列化算法
        Serializer serializer= SerializerManager.getSerializer(RpcHeader.getAlgorithmType());
        byte[] data=serializer.serialize(msg.getData());
        //设定数据长度
        out.writeInt(data.length);
        //设定数据
        out.writeBytes(data);
    }
}
