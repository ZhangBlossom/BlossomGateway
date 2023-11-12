package blossom.project.core.filter.monitor;

import blossom.project.core.ConfigLoader;
import blossom.project.core.context.GatewayContext;
import blossom.project.core.filter.Filter;
import blossom.project.core.filter.FilterAspect;
import com.alibaba.nacos.common.utils.RandomUtils;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static blossom.project.common.constant.FilterConst.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 实现了Filter接口的类，用于在应用程序中集成Prometheus监控。
 *
 * Prometheus出口过滤器
 */
@Slf4j
@FilterAspect(id = MONITOR_END_FILTER_ID, name = MONITOR_END_FILTER_NAME, order = MONITOR_END_FILTER_ORDER)
public class MonitorEndFilter implements Filter {
    /**
     * Prometheus监控的注册表实例，用于存储和管理监控指标。
     */
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    /**
     * 类构造器，初始化Prometheus监控和HTTP服务器。
     */
    public MonitorEndFilter() {
        // 创建PrometheusMeterRegistry实例，使用默认配置
        this.prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        try {
            // 创建一个新的HTTP服务器监听配置中指定的端口
            HttpServer server = HttpServer.create(new InetSocketAddress(ConfigLoader.getConfig().getPrometheusPort())
                    , 0);

            // 配置HTTP服务器处理路径"/prometheus"的请求，用于暴露监控数据
            server.createContext("/prometheus", exchange -> {
                // 获取Prometheus格式的监控数据
                String scrape = prometheusMeterRegistry.scrape();

                // 发送响应头，状态码200，内容长度为指标数据的字节长度
                exchange.sendResponseHeaders(200, scrape.getBytes().length);
                // 发送响应体，即指标数据
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(scrape.getBytes());
                }
            });

            // 启动HTTP服务器
            new Thread(server::start).start();

        } catch (IOException exception) {
            // 记录启动HTTP服务器失败的日志，并抛出运行时异常
            log.error("prometheus http server start error", exception);
            throw new RuntimeException(exception);
        }
        // 记录启动HTTP服务器成功的日志
        log.info("prometheus http server start successful, port:{}", ConfigLoader.getConfig().getPrometheusPort());

        // 使用mock数据定期更新Prometheus监控指标，以模拟应用程序负载
        Executors.newScheduledThreadPool(1000).scheduleAtFixedRate(() -> {
            // 创建Timer.Sample实例，开始计时
            Timer.Sample sample = Timer.start();
            try {
                // 模拟执行一个持续100到200毫秒的任务
                Thread.sleep(RandomUtils.nextInt(100, 200));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // TODO: 此处代码被注释，后续需根据实际情况实现具体的监控逻辑
            // 创建一个定制的Timer，记录请求的相关信息
             Timer timer = prometheusMeterRegistry.timer("gateway_request",
                    "uniqueId", "backend-http-server:1.0.0",
                    "protocol", "http",
                    "path", "/http-server/ping" + RandomUtils.nextInt(10, 200));
            // 停止计时器，并将数据记录到Prometheus注册表中
             sample.stop(timer);
        }, 200, 100, TimeUnit.MILLISECONDS); // 定时任务的初始延迟、周期和时间单位
        // ... (其他Filter接口必须实现的方法)
    }

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        // 创建一个计时器对象，用于测量操作执行时间
        Timer timer = prometheusMeterRegistry.timer("gateway_request",
                // 以下是多个标签用于标识和分类度量数据
                // "uniqueId" 标签，用于唯一标识请求，可能是请求的唯一标识符
                "uniqueId", ctx.getUniqueId(),
                // "protocol" 标签，表示请求的协议，可能是HTTP或其他协议
                "protocol", ctx.getProtocol(),
                // "path" 标签，表示请求的路径
                "path", ctx.getRequest().getPath());

        // 停止计时器，记录操作的执行时间
        ctx.getTimerSample().stop(timer);
    }

}
