package blossom.project.rpc.core.starter;

import blossom.project.rpc.core.handler.NettyRpcServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 19:42
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyRpcServer类
 */
@Slf4j
public class NettyRpcServer //implements LifeCycle
{

    //服务地址
    private String serverAddress;
    //端口
    private int serverPort;

    public NettyRpcServer(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start(){
        log.info("starting NettyRpcServer...");
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup work=new NioEventLoopGroup();

        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyRpcServerInitializer());
        try {
            ChannelFuture future=bootstrap.bind(this.serverAddress,this.serverPort).sync();
            log.info("start NettyRpcServer successfully,serverAddress: {} , serverPort: {}",serverAddress,serverPort);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    //// 服务器配置对象，用于获取如端口号等配置信息
    //private final Config config = new Config();
    //
    //// 服务器引导类，用于配置和启动Netty服务
    //private ServerBootstrap serverBootstrap;
    //// boss线程组，用于处理新的客户端连接
    //private EventLoopGroup eventLoopGroupBoss;
    //// worker线程组，用于处理已经建立的连接的后续操作
    //@Getter
    //private EventLoopGroup eventLoopGroupWoker;
    //
    //// 构造方法，用于创建Netty服务器实例
    //public NettyRpcServer(Config config) {
    //    init(); // 初始化服务器
    //}
    //
    //// 初始化服务器，设置线程组和选择线程模型
    //@Override
    //public void init() {
    //    this.serverBootstrap = new ServerBootstrap();
    //    // 判断是否使用Epoll模型，这是Linux系统下的高性能网络通信模型
    //    if (useEpoll()) {
    //        this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(),
    //                new DefaultThreadFactory("epoll-netty-boss-nio"));
    //        this.eventLoopGroupWoker = new EpollEventLoopGroup(config.getEventLoopGroupWokerNum(),
    //                new DefaultThreadFactory("epoll-netty-woker-nio"));
    //    } else {
    //        // 否则使用默认的NIO模型
    //        this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),
    //                new DefaultThreadFactory("nio-netty-boss-nio"));
    //        this.eventLoopGroupWoker = new NioEventLoopGroup(config.getEventLoopGroupWokerNum(),
    //                new DefaultThreadFactory("nio-netty-woker-nio"));
    //    }
    //}
    //
    //// 检测是否使用Epoll优化性能
    //public boolean useEpoll() {
    //    return RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    //}
    //
    //// 启动Netty服务器
    //@Override
    //public void start() {
    //    // 配置服务器参数，如端口、TCP参数等
    //    this.serverBootstrap
    //            .group(eventLoopGroupBoss, eventLoopGroupWoker)
    //            .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
    //            .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
    //            .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
    //            .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
    //            .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
    //            .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小
    //            .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
    //            .localAddress(new InetSocketAddress(config.getPort())) // 绑定监听端口
    //            .childHandler(new NettyRpcServerInitializer());
    //    // 绑定端口并启动服务，等待服务端关闭
    //    try {
    //        this.serverBootstrap.bind().sync();
    //        //也可以用这种方法进行netty端口监听的绑定
    //        //this.serverBootstrap.bind(config.getPort()).sync();
    //        log.info("server startup on port {}", this.config.getPort());
    //    } catch (Exception e) {
    //        throw new RuntimeException("启动服务器时发生异常", e);
    //    }
    //}
    //
    //// 关闭Netty服务器，释放资源
    //@Override
    //public void shutdown() {
    //    if (eventLoopGroupBoss != null) {
    //        eventLoopGroupBoss.shutdownGracefully(); // 优雅关闭boss线程组
    //    }
    //
    //    if (eventLoopGroupWoker != null) {
    //        eventLoopGroupWoker.shutdownGracefully(); // 优雅关闭worker线程组
    //    }
    //}
}
