package blossom.project.rpc.core.starter;

import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcRequest;
import blossom.project.rpc.core.handler.NettyRpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import java.io.IOException;

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
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    //服务地址
    private String serviceAddress;
    private int servicePort;

    public NettyRpcClient(String serviceAddress, int servicePort) {
        log.info("starting NettyRpcClient...");
        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyRpcClientInitializer());
        this.serviceAddress = serviceAddress;
        this.servicePort = servicePort;
        log.info("start NettyClient successfully,serviceAddress: {} , servivePort: {}",
                serviceAddress,servicePort);
    }

    public void sendRequest(RpcDto<RpcRequest> protocol) throws InterruptedException {
        final ChannelFuture future = bootstrap
                .connect(this.serviceAddress, this.servicePort).sync();
        future.addListener(listener -> {
            if (future.isSuccess()) {
                log.info("connect to NettyRpcServer successfully...", this.serviceAddress);
            } else {
                log.error("connect to NettyRpcServer failed...", this.serviceAddress);
                future.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        log.info("begin transfer data");
        future.channel().writeAndFlush(protocol);
    }
}
