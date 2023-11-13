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