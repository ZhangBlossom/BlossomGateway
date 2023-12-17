package blossom.project.rpc.core.handler;

import blossom.project.rpc.core.codec.RpcDecode;
import blossom.project.rpc.core.codec.RpcEncode;
import blossom.project.rpc.core.constants.RpcCommonConstants;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 19:44
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyRpcClientHandler类
 */
@Slf4j
public class NettyRpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("---The system starts to initialize the <NettyRpcClient>---");
        ch.pipeline().addLast(
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                //拒绝硬编码从我做起
                                RpcCommonConstants.HEADER_LENGTH-4,
                                4,
                                0,
                                0))
                .addLast(new LoggingHandler())
                .addLast(new RpcEncode())
                .addLast(new RpcDecode())
                .addLast(new NettyRpcClientHandler());
    }
}
