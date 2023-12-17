package blossom.project.rpc.core.starter;

import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcRequest;
import blossom.project.rpc.core.handler.NettyRpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 19:42
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyRpcClient类
 */
@Slf4j
public class NettyRpcClient {
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    //TODO 目标服务地址，暂时先硬编码 后续考虑用注册中心
    private String serviceAddress;
    private int servicePort;

    public NettyRpcClient(String serviceAddress, int servicePort) {
        log.info("starting NettyRpcClient...");

        this.serviceAddress = serviceAddress;
        this.servicePort = servicePort;

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyRpcClientInitializer());

        log.info("start NettyClient successfully！！！ , serviceAddress: {} , servivePort: {}", serviceAddress,
                servicePort);
    }

    /**
     * 当前方法用于发送我们组装好地请求数据
     * @param requestRpcDto
     * @throws InterruptedException
     */
    public void doRequest(RpcDto<RpcRequest> requestRpcDto) {
        final ChannelFuture future;
        try {
            // 连接到服务器。这里使用了bootstrap对象（预先配置好的客户端启动器）来建立连接。
            // 连接到指定的服务地址和端口，并且同步等待直到连接完成。
            future = bootstrap
                    .connect(this.serviceAddress, this.servicePort)
                    .sync();

            // 给future添加监听器，用于处理连接操作的结果。
            future.addListener(listener -> {
                // 如果连接成功，记录一条日志信息。
                if (future.isSuccess()) {
                    log.info("Connect to NettyRpcServer successfully...", this.serviceAddress);
                } else {
                    // 如果连接失败，记录错误日志，并打印异常堆栈，然后优雅地关闭事件循环组。
                    log.error("Connect to NettyRpcServer failed...", this.serviceAddress);
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });

            // 连接建立后，开始数据传输。将RpcDto<RpcRequest>对象发送到服务器。
            log.info("Start data transmission...");
            // 特别注意一下这里的writeAndFlush的返回值是ChannelFuture
            // ChannelFuture 提供了一种处理异步操作结果的方式。
            // 可以检查操作是否成功，如果操作失败，可以获取到失败的原因。
            //TODO 构思一下对ChannelFuture的处理
            ChannelFuture sendFuture = future.channel().writeAndFlush(requestRpcDto);

            // 添加监听器处理writeAndFlush的结果
            // 1：添加监听器异步等待
            //sendFuture.addListener(new ChannelFutureListener() {
            //    @Override
            //    public void operationComplete(ChannelFuture future) throws Exception {
            //        if (future.isSuccess()) {
            //            // 写操作成功
            //            log.info("Message send successfully...");
            //        } else {
            //            // 写操作失败，处理错误
            //            log.error("Failed to send message", future.cause());
            //        }
            //    }
            //});
            //Void unused = sendFuture.get();
            //TODO 这里写出这些代码只是为了帮助你了解还有很多种
            //方式可以处理ChannelFuture 不过这里中方法是同步的
            //违背了Netty的异步处理数据的初衷 所以知道就好
            // 2: 同步等待直到写操作完成
            //sendFuture.sync();
            //3: 等待写操作完成，但不抛出中断异常
            //sendFuture.await();
            // 2/3 两种方法的检查机制 检查操作是否成功
            //if (sendFuture.isSuccess()) {
            //    log.info("Message sent successfully");
            //} else {
            //    log.error("Failed to send message", sendFuture.cause());
            //}


        } catch (InterruptedException e) {
            // 如果连接过程中发生中断异常，打印异常堆栈并抛出运行时异常。
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
