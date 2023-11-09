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
 * Prometheus出口过滤器
 *
 */
@Slf4j
//@FilterAspect(id = MONITOR_END_FILTER_ID,
//        name = MONITOR_END_FILTER_NAME,
//        order = MONITOR_END_FILTER_ORDER)
public class MonitorEndFilter implements Filter {
    /**
     * 普罗米修斯的注册表
     */
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    public MonitorEndFilter() {
        this.prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        try {
            //暴露接口来提供普罗米修斯指标数据拉取
            HttpServer server = HttpServer.create(new InetSocketAddress(ConfigLoader.getConfig().getPrometheusPort())
                    , 0);
            server.createContext("/prometheus", exchange -> {
                //获取指标数据的文本内容
                String scrape = prometheusMeterRegistry.scrape();

                //指标数据返回
                exchange.sendResponseHeaders(200, scrape.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(scrape.getBytes());
                }
            });

            new Thread(server::start).start();

        } catch (IOException exception) {
            log.error("prometheus http server start error", exception);
            throw new RuntimeException(exception);
        }
        log.info("prometheus http server start successful, port:{}", ConfigLoader.getConfig().getPrometheusPort());

        //mock
        Executors.newScheduledThreadPool(1000).scheduleAtFixedRate(() -> {
            Timer.Sample sample = Timer.start();
            try {
                Thread.sleep(RandomUtils.nextInt(100, 200));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //TODO 这里直接写死代码 mock一下 后续需要修改
            Timer timer = prometheusMeterRegistry.timer("gateway_request",
                    "uniqueId", "backend-http-server:1.0.0",
                    "protocol", "http",
                    "path", "/http-server/ping" + RandomUtils.nextInt(10, 200));
            sample.stop(timer);
        }, 200, 100, TimeUnit.MILLISECONDS);
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
