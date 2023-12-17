package blossom.project.rpc.core.handler;

import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcCache;
import blossom.project.rpc.core.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/17 02:43
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyRpcServerHandler类
 *
 * 备忘录
 * 有点烧脑 分析一下这个类怎么用 先睡了
 * 1.1：当前类是客户端接收到服务器的response了
 * 1.2：如果没有报错，那么我就要从我的cache中拿到
 * 我特定reqId对应的promise
 * 1.3：设定promise的值
 * 1.3.1：promise一旦被设定，promise.get()的阻塞马上就会结束
 * 1.3.2：也就是我成功拿到了Server的响应值
 * 1.3.3：那么Client的这次调用就是成功的
 * 1.3.4：否则失败
 * 1.4：删除promise再缓存中的reqId
 * 1.5：这里如果对future/promise进行设置值之后，代理应该马上返回
 * 1.6：用promise的setXxx类型方法比较合适
 *
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcDto<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDto<RpcResponse> msg) throws Exception {
        if (Objects.isNull(msg)){
            log.info("the RpcDto<Response> is Null,return...");
            return;
        }
        log.info("receive the Rpc Server Data, msg is: {}",msg);
        long reqId = msg.getHeader().getReqId();
        //TODO 得到并且删除 考虑一下DefaultPromise是否需要封装
        DefaultPromise defaultPromise = RpcCache
                .RESPONSE_CACHE.remove(reqId);
        defaultPromise.setSuccess(msg.getData());

    }
}
