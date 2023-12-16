package blossom.project.rpc.core.codec;

import blossom.project.rpc.core.constants.RpcCommonConstants;
import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcHeader;
import blossom.project.rpc.core.entity.RpcRequest;
import blossom.project.rpc.core.entity.RpcResponse;
import blossom.project.rpc.core.enums.ReqTypeEnum;
import blossom.project.rpc.core.serialize.Serializer;
import blossom.project.rpc.core.serialize.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 18:58
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcDecode类
 * 平平无奇RPC自定义协议的解码器
 */
@Slf4j
public class RpcDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("Start decoding the data");
        //判断请求头合法性
        if (Objects.isNull(in)) {
            log.warn("the data is Null!!!");
            return;
        }
        //因为我们的数据至少包含请求头+请求体 因此长度小于此肯定有问题
        if (in.readableBytes() < RpcCommonConstants.HEADER_LENGTH) {
            log.warn("The length of the request header does not meet the requirements!!!");
            return;
        }
        //读取版本号
        byte versionId = in.readByte();
        if (versionId != RpcCommonConstants.VERSION_ID) {
            throw new IllegalArgumentException("Illegal versionId!!!");
        }
        //继续读取请求头信息
        byte algorithmType = in.readByte();
        byte reqType = in.readByte();
        long reqId = in.readLong();
        int length = in.readInt();
        //判断可读长度是否小于请求头中设定的请求体长度
        if (in.readableBytes() < length) {
            //数据长度不对劲 先丢掉
            log.info("the readable bytes's length is less!!!");
            return;
        }
        RpcHeader header = new RpcHeader(
                versionId, algorithmType, reqType, reqId, length);
        //获得反序列化器
        Serializer serializer = SerializerManager.getSerializer(algorithmType);
        //获得请求类型
        ReqTypeEnum reqTypeEnum = ReqTypeEnum.getReqTypeByCode(reqType);
        //得到实际传输的数据
        byte[] data = new byte[length];
        in.readBytes(data);
        RpcDto dto = null;
        switch (reqTypeEnum) {
            case REQUEST:
                // 处理 GET 请求逻辑
                log.info("Handling REQUEST request");
                RpcRequest request =
                        serializer.deserialize(data, RpcRequest.class);
                dto = new RpcDto<RpcRequest>();
                dto.setHeader(header);
                dto.setData(request);
                //设定到out中 会自动被handler处理
                out.add(dto);
                break;
            case RESPONSE:
                // 处理 RESPONSE 请求逻辑
                log.info("Handling RESPONSE request");
                RpcResponse response =
                        serializer.deserialize(data, RpcResponse.class);
                dto = new RpcDto<RpcResponse>();
                dto.setHeader(header);
                dto.setData(response);
                //设定到out中 会自动被handler处理
                out.add(dto);
                break;
            case HEARTBEAT:
                // 处理心跳检测逻辑
                log.info("Handling HEARTBEAT request");
                break;
            default:
                log.warn("Unsupported request type: " + reqTypeEnum);
        }
    }
}
