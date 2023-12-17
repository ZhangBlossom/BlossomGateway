package blossom.project.rpc.core.handler;

import blossom.project.rpc.core.codec.RpcDecode;
import blossom.project.rpc.core.codec.RpcEncode;
import blossom.project.rpc.core.constants.RpcCommonConstants;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 19:43
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyRpcServerHandler类
 */
@Slf4j
public class NettyRpcServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("---The system starts to initialize the <NettyRpcServer>---");
        ch.pipeline().
                addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                        RpcCommonConstants.HEADER_LENGTH-4,
                        4,
                        0,
                        0))
                .addLast(new RpcDecode())
                .addLast(new RpcEncode())
                .addLast(new NettyRpcServerHandler());
    }
}
