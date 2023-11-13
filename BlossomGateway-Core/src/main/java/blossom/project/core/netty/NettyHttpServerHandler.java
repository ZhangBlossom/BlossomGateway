package blossom.project.core.netty;

import blossom.project.core.context.HttpRequestWrapper;
import blossom.project.core.netty.processor.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom

 * NettyHttpServerHandler 用于处理通过 Netty 传入的 HTTP 请求。
 * 它继承自 ChannelInboundHandlerAdapter，这样可以覆盖回调方法来处理入站事件。
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    // 成员变量nettyProcessor，用于处理具体的业务逻辑
    private final NettyProcessor nettyProcessor;

    /**
     * 构造函数，接收一个 NettyProcessor 类型的参数。
     *
     * @param nettyProcessor 用于处理请求的业务逻辑处理器。
     */
    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    /**
     * 当从客户端接收到数据时，该方法会被调用。
     * 这里将入站的数据（HTTP请求）包装后，传递给业务逻辑处理器。
     *
     * @param ctx ChannelHandlerContext，提供了操作网络通道的方法。
     * @param msg 接收到的消息，预期是一个 FullHttpRequest 对象。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将接收到的消息转换为 FullHttpRequest 对象
        FullHttpRequest request = (FullHttpRequest) msg;
        // 创建 HttpRequestWrapper 对象，并设置上下文和请求
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
        httpRequestWrapper.setCtx(ctx);
        httpRequestWrapper.setRequest(request);

        // 调用业务逻辑处理器的 process 方法处理请求
        nettyProcessor.process(httpRequestWrapper);
    }

    /**
     * 处理在处理入站事件时发生的异常。
     *
     * @param ctx   ChannelHandlerContext，提供了操作网络通道的方法。
     * @param cause 异常对象。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 调用父类的 exceptionCaught 方法，它将按照 ChannelPipeline 中的下一个处理器继续处理异常
        super.exceptionCaught(ctx, cause);
        // 打印自定义消息，实际使用时应该记录日志或进行更复杂的异常处理
        System.out.println("----");
    }
}