# 项目骨架的搭建

​		在上一节中已经设定好了大致的网关架构所需要用到的一些服务，比如Common、Register Center、Config Center、Client、Core等模块，那么我们就按照这样子框架先搭建出来一个项目骨架。

​		搭建完毕之后，项目骨架如下：

​		![](D:\desktop\BlossomGateway\photos\项目骨架.png)	

​		经过上一章节的讲解，我想只需要根据我们的目录名称就可以大概知道这些目录的作用。

​		但是我还是打算介绍一下这些目录。

- Client：网关服务客户端，提供了一些注解用于注册对应的服务到网关中。

- Common：网关通用模块包，存放通用配置以及一些工具类。

- ConfigCenter：网关配置中心，当前包提供基于Nacos服务的配置中心的功能，负责从配置中心拉取对应的配置。

- RegisterCenter：网关注册中心，用于将网关服务注册到基于Nacos的注册中心。

- Core：网关核心模块，网关的几乎所有功能以及网关的启动都依赖于这个模块，包含了过滤器、启动容器、Netty网络处理框架等核心代码。

- HttpServer：测试网关HTTP请求服务模块。

- UserServer：测试网关JWT安全鉴权服务模块。

- Dubbo：提供网关基于Dubbo的RPC远程服务调用实现（暂未开发）。

  

  

  

  

  # 基于Netty的网络通信层设计

  

  ## Netty概述

  ​		在基于Netty进行设计之前，我们先按照老方法，介绍一下Netty，以及它的作用和使用场景。

  1. **Netty 是什么**: Netty 是一个高性能的、异步事件驱动的网络应用程序框架，支持快速开发可维护的高性能协议服务器和客户端。它是一个在 Java NIO 的基础上构建的网络编程框架，提供了易于使用的API。
  2. **Netty 的具体功能**:
     - **异步和事件驱动**：Netty 提供了一个多线程的事件循环，用于处理所有网络事件，例如连接、数据发送和接收。
     - **支持多协议**：它可以支持多种传输协议，包括 TCP、UDP，以及更高级的协议如 HTTP、HTTPS、WebSocket。
     - **高度可定制**：可以通过ChannelHandler来定制处理网络事件的逻辑，支持编解码器、拦截器等。
     - **性能优化**：利用池化和复用技术来减少资源消耗，减少GC压力，优化内存使用。
     - **安全性**：内置了对 SSL/TLS 协议的支持，确保数据传输安全。
  3. **Netty 中的核心概念**:
     - **Boss 和 Worker 线程**：在 Netty 的服务器端，"Boss" 线程负责处理连接的建立，而 "Worker" 线程负责处理已连接的通道的IO操作。这种模型允许Boss线程迅速处理新的连接，并将数据传输的处理任务委托给Worker线程。
     - **Channel**：代表一个到远程节点的开放连接，可以进行读写操作。
     - **EventLoop**：用于处理连接的生命周期中的所有事件，每个Channel都分配给了一个EventLoop。
     - **ChannelHandler**：核心处理器，可以响应入站和/或出站事件和数据。
  4. **Netty 的使用场景**:
     - **Web服务器和客户端**：使用Netty作为底层通信组件来构建自己的Web服务器和HTTP客户端。
     - **实时通信系统**：如在线游戏的服务器、聊天服务器，因为Netty支持WebSocket和TCP协议，适合需要低延迟和大量并发连接的应用。

## Netty实战

首先，在 Maven `pom.xml` 文件中引入Netty的依赖：

```java
<dependencies>
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.59.Final</version>
    	<!-- 使用最新版本 -->
    </dependency>
</dependencies>
```

## 服务端实现

​	在基于 Netty设计 服务端之前我们首先需要了解一下几个Netty中的核心概念：

1. **`EventLoopGroup`**: 这是Netty中的一个核心组件，负责处理所有的I/O操作。`EventLoopGroup`是一个包含多个`EventLoop`的组，每个`EventLoop`都是一个单线程循环，负责处理连接的生命周期内的所有事件。其分为boss和worker两种类型的线程组。boss线程组通常负责接受新的客户端连接，而worker线程组负责处理boss线程组接受的连接的后续I/O操作。
2. **`ServerBootstrap`**: 这个类是一个帮助类，用于设置服务器。它允许我们设置服务器所需的所有参数，如端口、使用的`EventLoopGroup`等。`ServerBootstrap`还允许为新接受的连接以及连接后的通道设置属性和处理程序。
3. **`Channel`**: `Channel`接口代表一个到远程节点的开放连接，可以进行读写操作。在Netty中，`Channel`是网络通信的基础组件，每个连接都会创建一个新的`Channel`。
4. **`ChannelInitializer`**: 这是一个特殊的处理程序，用于配置新注册的`Channel`的`ChannelPipeline`，它提供了一个容易扩展的方式来初始化`Channel`，一旦`Channel`注册到`EventLoop`上，就会调用`ChannelInitializer`。
5. **`ChannelPipeline`**: 这个接口表示一个`ChannelHandler`的链表，用于处理或拦截入站和出站操作。它使得可以容易地添加或删除处理程序。
6. **`ChannelHandler`**: 接口定义了很多事件处理方法，你可以通过实现这些方法来进行自定义的事件处理。事件可以是入站也可以是出站的，例如数据读取、写入、连接开启和关闭。
7. **`ChannelHandlerContext`**: 提供了一个接口，用于在`ChannelHandler`中进行交互操作。通过这个上下文对象，处理程序可以传递事件、修改管道、存储处理信息等。
8. **`ChannelOption`** 和 **`ChannelConfig`**: 这些类和接口用于配置`Channel`的参数，如连接超时、缓冲区大小等。
9. **`NioEventLoopGroup`** 和 **`EpollEventLoopGroup`**: 这些类是`EventLoopGroup`的实现，分别对应于使用Java NIO和Epoll（只在Linux上可用）作为传输类型。Netty自动选择使用哪个实现，通常基于操作系统的能力和应用程序的需求。
10. **`NioServerSocketChannel`** 和 **`EpollServerSocketChannel`**: 这些是`Channel`实现，表示服务器端的套接字通道。选择哪个实现通常取决于你选择的`EventLoopGroup`。
11. **编解码器（`Codec`）**: Netty提供了一系列的编解码器用于数据的编码和解码，例如`HttpServerCodec`用于HTTP协议的编码和解码。

​		在你已经大致的知道了设计一个Netty客户端所涉及到的一些知识之后，我们来基于代码进行分析。

​	

```java
package blossom.project.core.netty;

import blossom.project.common.utils.RemotingUtil;
import blossom.project.core.Config;
import blossom.project.core.LifeCycle;
import blossom.project.core.netty.processor.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


import java.net.InetSocketAddress;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/24 19:23
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Netty的Server端实现
 */


// 类注解和作者信息
@Slf4j
public class NettyHttpServer implements LifeCycle {
    // 服务器配置对象，用于获取如端口号等配置信息
    private final Config config;
    // 自定义的Netty处理器接口，用于定义如何处理接收到的请求
    private final NettyProcessor nettyProcessor;
    // 服务器引导类，用于配置和启动Netty服务
    private ServerBootstrap serverBootstrap;
    // boss线程组，用于处理新的客户端连接
    private EventLoopGroup eventLoopGroupBoss;
    // worker线程组，用于处理已经建立的连接的后续操作
    @Getter
    private EventLoopGroup eventLoopGroupWoker;

    // 构造方法，用于创建Netty服务器实例
    public NettyHttpServer(Config config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init(); // 初始化服务器
    }

    // 初始化服务器，设置线程组和选择线程模型
    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();
        // 判断是否使用Epoll模型，这是Linux系统下的高性能网络通信模型
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("epoll-netty-boss-nio"));
            this.eventLoopGroupWoker = new EpollEventLoopGroup(config.getEventLoopGroupWokerNum(),
                    new DefaultThreadFactory("epoll-netty-woker-nio"));
        } else {
            // 否则使用默认的NIO模型
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("default-netty-boss-nio"));
            this.eventLoopGroupWoker = new NioEventLoopGroup(config.getEventLoopGroupWokerNum(),
                    new DefaultThreadFactory("default-netty-woker-nio"));
        }
    }

    // 检测是否使用Epoll优化性能
    public boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }

    // 启动Netty服务器
    @Override
    public void start() {
        // 配置服务器参数，如端口、TCP参数等
        this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWoker)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
                .localAddress(new InetSocketAddress(config.getPort())) // 绑定监听端口
                .childHandler(new ChannelInitializer<Channel>() {   // 定义处理新连接的管道初始化逻辑
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // 配置管道中的处理器，如编解码器和自定义处理器
                        ch.pipeline().addLast(
                                new HttpServerCodec(), // 处理HTTP请求的编解码器
                                new HttpObjectAggregator(config.getMaxContentLength()), // 聚合HTTP请求
                                new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                new NettyHttpServerHandler(nettyProcessor), // 自定义的处理器
                                new NettyServerConnectManagerHandler() // 连接管理处理器
                        );
                    }
                });

        // 绑定端口并启动服务，等待服务端关闭
        try {
            this.serverBootstrap.bind().sync();
            log.info("server startup on port {}", this.config.getPort());
        } catch (Exception e) {
            throw new RuntimeException("启动服务器时发生异常", e);
        }
    }

    // 关闭Netty服务器，释放资源
    @Override
    public void shutdown() {
        if (eventLoopGroupBoss != null) {
            eventLoopGroupBoss.shutdownGracefully(); // 优雅关闭boss线程组
        }

        if (eventLoopGroupWoker != null) {
            eventLoopGroupWoker.shutdownGracefully(); // 优雅关闭worker线程组
        }
    }
}

```

​		大部分地方都比较容易理解，在init方法中我们初始化了EventLoopGroup来帮助我们处理我们的IO请求。

​	在start这个重点方法中我们基于ServerBootStrap进行了对Netty的配置。

​	我们依靠ChannelInitializer来添加通道处理类。

​	在这个ChannelInitializer中，只有两行代码是最重要的：

```java
 new NettyHttpServerHandler(nettyProcessor), // 自定义的处理器
 new NettyServerConnectManagerHandler() // 连接管理处理器
```

​	这行代码意味着进入到Netty通道中的请求需要被我的这个自定义的处理器类所处理。

​	所以我们来分析一下这个处理器类都起到了什么作用。

​	首先是先分析NettyHttpServerHandler这个类是什么，它的作用是什么：

```
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
```

​	ChannelInboundHandlerAdapter提供了一种简便的方式来帮助我们处理入站的网络事件。	

​	其中channelRead比较重要，它可以帮助我解析和处理接收到的数据，因为在Netty中，消息通常是 `ByteBuf` 的形式，但也可以是任何我在 `ChannelPipeline` 中设置的解码器能够处理的类型。因此我可以在这个方法中实现我的业务逻辑，也可以调用业务逻辑处理器来处理我接受到的数据。

​	我们可以Debug看看这个发送一个请求的时候，在这个地方都发生了什么。

![](D:\desktop\BlossomGateway\photos\channelRead方法.png)

​	可以看到请求和FullHttpRequest有着巨大的关系，在Netty框架中，`FullHttpRequest` 类是一个接口，它代表了一个完整的HTTP请求。这个类的作用是封装了HTTP请求的所有部分，包括请求行（如方法GET/POST、URI、HTTP版本）、请求头（Headers）以及请求体（Body）。因此我们只要拿到了这个类的信息并且保存，我们就可以在后续随时的对这一次的请求信息进行分析并做出对应的处理。

​	再来看看上面提到的ChannelHandlerContext。可以看到他有点类似于过滤器链，指向了下一个要处理当前请求的类。它的作用在上面我也已经讲解到了，详细的讲解就先留一个伏笔吧。

![](D:\desktop\BlossomGateway\photos\ChannelHandlerContext.png)

​	然后我们来分析这里的NettyProcessor这个接口的实现类的作用。

​	在没有使用Disruptor之前，我们来看看默认我的实现：

```java
  @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();

        try {
            // 创建并填充 GatewayContext 以保存有关传入请求的信息。
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);

            // 在 GatewayContext 上执行过滤器链逻辑。
            filterFactory.buildFilterChain(gatewayContext).doFilter(gatewayContext);
        } catch (BaseException e) {
            // 通过记录日志并发送适当的 HTTP 响应处理已知异常。
            log.error("处理错误 {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            // 通过记录日志并发送内部服务器错误响应处理未知异常。
            log.error("处理未知错误", t);
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }
    }
```

​		可以发现，请求在走到这里的时候，其实接下来就即将开始我的正式对请求的处理过程了，也就是保存并填充我的网关请求上下文，也就是这里的GatewayContext，这个类中包含了当前请求所需要使用的规则，请求体与响应体。保存这个信息是为了接下来后续方便我对请求的不同规则有不同的处理。

​		不过我并没有在这里就打算马上开始讲解对请求的过滤器链的处理，因为这一节我将侧重在Netty这一块的设计。

​		那么接下来我们来看Netty对于网络连接这一块的处理吧。

## 网络链接管理

​		代码中已经比较详细的讲解了这个类的作用。它负责对我们网络请求链接的生命周期进行处理。

​		这个类对于我们的设计并不是最重要的，所以这里我选择一笔带过这个类。自行查看代码并且进行Debug了解一下什么时候会执行这个类中的方法即可。

​		比如第一次发送请求创建链接的时候就会调用register和active方法。

```java
package blossom.project.core.netty;

import blossom.project.common.utils.RemotingHelper;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/24 13:57
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

```

## 客户端实现

​	由于前面我已经在服务端实现的时候列举了所需要的一些关键的组件，而客户端的实现所需要用到的也差不多，所以就不在重复罗列。

​	这里我讲解一下实现一个异步的HTTP通信客户端如何去实现，这里我用到的是 `AsyncHttpClient` 这样的高层库，它基于Netty构建，提供了异步的HTTP客户端功能，它可以非阻塞地发送HTTP请求，并且能够高效地处理HTTP响应。

​	讲解了这些，我们再来分析我们的代码就会比较容易理解了：

```java
package blossom.project.core.netty;

import blossom.project.core.Config;
import blossom.project.core.LifeCycle;
import blossom.project.core.helper.AsyncHttpHelper;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import java.io.IOException;
/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 12:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 *
 * NettyHttpClient 类负责创建和管理基于Netty的异步HTTP客户端。
 * 它实现了LifeCycle接口，以提供初始化、启动和关闭客户端的方法。
 */
@Slf4j
public class NettyHttpClient implements LifeCycle {
    // 配置信息对象，包含HTTP客户端的配置参数
    private final Config config;
    // Netty的事件循环组，用于处理客户端的网络事件
    private final EventLoopGroup eventLoopGroupWoker;
    // 异步HTTP客户端实例
    private AsyncHttpClient asyncHttpClient;

    /**
     * 构造函数，创建NettyHttpClient的实例。
     *
     * @param config           包含客户端配置的对象。
     * @param eventLoopGroupWoker 用于客户端事件处理的Netty事件循环组。
     */
    public NettyHttpClient(Config config, EventLoopGroup eventLoopGroupWoker) {
        this.config = config;
        this.eventLoopGroupWoker = eventLoopGroupWoker;
        init();  // 初始化客户端
    }

    /**
     * 初始化异步HTTP客户端，设置其配置参数。
     */
    @Override
    public void init() {
        // 创建异步HTTP客户端配置的构建器
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setEventLoopGroup(eventLoopGroupWoker) // 使用传入的Netty事件循环组
                .setConnectTimeout(config.getHttpConnectTimeout()) // 连接超时设置
                .setRequestTimeout(config.getHttpRequestTimeout()) // 请求超时设置
                .setMaxRedirects(config.getHttpMaxRequestRetry()) // 最大重定向次数
                .setAllocator(PooledByteBufAllocator.DEFAULT) // 使用池化的ByteBuf分配器以提升性能
                .setCompressionEnforced(true) // 强制压缩
                .setMaxConnections(config.getHttpMaxConnections()) // 最大连接数
                .setMaxConnectionsPerHost(config.getHttpConnectionsPerHost()) // 每个主机的最大连接数
                .setPooledConnectionIdleTimeout(config.getHttpPooledConnectionIdleTimeout()); // 连接池中空闲连接的超时时间
        // 根据配置创建异步HTTP客户端
        this.asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
    }

    /**
     * 启动客户端，通常在这里进行资源分配和启动必要的服务。
     */
    @Override
    public void start() {
        // 使用AsyncHttpHelper单例模式初始化异步HTTP客户端
        AsyncHttpHelper.getInstance().initialized(asyncHttpClient);
    }

    /**
     * 关闭客户端，通常在这里进行资源释放和清理工作。
     */
    @Override
    public void shutdown() {
        // 如果客户端实例不为空，则尝试关闭它
        if (asyncHttpClient != null) {
            try {
                // 关闭客户端，并处理可能的异常
                this.asyncHttpClient.close();
            } catch (IOException e) {
                // 记录关闭时发生的错误
                log.error("NettyHttpClient shutdown error", e);
            }
        }
    }
}
```

​	我的客户端就是基于AsyncHttpClient这样子的高层库实现的异步数据交互。

​	其实到此，对于Netty这一块的设计基本就已经简述了，讲述的比较简单，但是我想可以帮助你顺利的去理解如何基于Netty实现一个网络通信。

