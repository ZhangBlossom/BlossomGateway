package blossom.project.rpc.core.handler;

import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcDto<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDto<RpcResponse> msg) throws Exception {
        log.info("receive Rpc Server Result");
        long requestId = msg.getHeader().getReqId();
        //RpcFuture<RpcResponse> future = RequestHolder.REQUEST_MAP.remove(requestId);
        //future.getPromise().setSuccess(msg.getContent());
    }
}
