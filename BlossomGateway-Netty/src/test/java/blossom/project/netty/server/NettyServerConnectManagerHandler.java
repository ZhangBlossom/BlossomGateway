package blossom.project.netty.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 连接管理器，管理连接对生命周期
 * 当前类提供出站和入站事件的处理能力，能够管理网络链接的整个生命周期
 * 服务器连接管理器，用于监控和管理网络连接的生命周期事件。
 */
@Slf4j
public class NettyServerConnectManagerHandler extends ChannelDuplexHandler {

    /**
     * 当通道被注册到它的EventLoop时调用，即它可以开始处理I/O事件。
     *
     * @param ctx 提供了操作网络通道的方法的上下文对象。
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // 获取远程客户端的地址
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        // 记录调试信息
        log.debug("NETTY SERVER PIPLINE: channelRegistered {}", remoteAddr);
        // 调用父类方法继续处理注册事件
        super.channelRegistered(ctx);
    }

    /**
     * 当通道从它的EventLoop注销时调用，不再处理任何I/O事件。
     *
     * @param ctx 提供了操作网络通道的方法的上下文对象。
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelUnregistered {}", remoteAddr);
        super.channelUnregistered(ctx);
    }

    /**
     * 当通道变为活跃状态，即连接到远程节点时被调用。
     *
     * @param ctx 提供了操作网络通道的方法的上下文对象。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelActive {}", remoteAddr);
        super.channelActive(ctx);
    }

    /**
     * 当通道变为不活跃状态，即不再连接远程节点时被调用。
     *
     * @param ctx 提供了操作网络通道的方法的上下文对象。
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        log.debug("NETTY SERVER PIPLINE: channelInactive {}", remoteAddr);
        super.channelInactive(ctx);
    }

    /**
     * 当用户自定义事件被触发时调用，例如，可以用来处理空闲状态检测事件。
     *
     * @param ctx 提供了操作网络通道的方法的上下文对象。
     * @param evt 触发的用户事件。
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 检查事件是否为IdleStateEvent（空闲状态事件）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 如果是所有类型的空闲事件，则关闭通道
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                log.warn("NETTY SERVER PIPLINE: userEventTriggered: IDLE {}", remoteAddr);
                ctx.channel().close();
            }
        }
        // 传递事件给下一个ChannelHandler
        ctx.fireUserEventTriggered(evt);
    }

    /**
     * 当处理过程中发生异常时调用，通常是网络层面的异常。
     *
     * @param ctx   提供了操作网络通道的方法的上下文对象。
     * @param cause 异常对象。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        // 记录警告信息和异常堆栈
        log.warn("NETTY SERVER PIPLINE: remoteAddr： {}, exceptionCaught {}", remoteAddr, cause);
        // 发生异常时关闭通道
        ctx.channel().close();
    }
}
