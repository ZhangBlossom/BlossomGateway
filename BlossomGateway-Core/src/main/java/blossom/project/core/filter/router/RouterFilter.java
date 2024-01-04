package blossom.project.core.filter.router;

import blossom.project.common.config.Rule;
import blossom.project.common.enums.ResponseCode;
import blossom.project.common.exception.ConnectException;
import blossom.project.common.exception.ResponseException;
import blossom.project.core.ConfigLoader;
import blossom.project.core.context.GatewayContext;
import blossom.project.core.filter.Filter;
import blossom.project.core.filter.FilterAspect;
import blossom.project.core.helper.AsyncHttpHelper;
import blossom.project.core.helper.ResponseHelper;
import blossom.project.core.response.GatewayResponse;
import com.netflix.hystrix.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static blossom.project.common.constant.FilterConst.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 12:51
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RouterFilter类
 * 路由过滤器 执行路由转发操作  --- 网关核心过滤器
 */
@Slf4j
@FilterAspect(id = ROUTER_FILTER_ID, name = ROUTER_FILTER_NAME, order = ROUTER_FILTER_ORDER)
public class RouterFilter implements Filter {

    private static Logger accessLog = LoggerFactory.getLogger("accessLog");

    @Override
    public void doFilter(GatewayContext gatewayContext) throws Exception {
        //首先获取熔断降级的配置
        Optional<Rule.HystrixConfig> hystrixConfig = getHystrixConfig(gatewayContext);
        //如果存在对应配置就走熔断降级的逻辑
        if (hystrixConfig.isPresent()) {
            routeWithHystrix(gatewayContext, hystrixConfig);
        } else {
            route(gatewayContext, hystrixConfig);
        }
    }

    /**
     * 获取hystrix的配置
     *
     * @param gatewayContext
     * @return
     */
    private static Optional<Rule.HystrixConfig> getHystrixConfig(GatewayContext gatewayContext) {
        Rule rule = gatewayContext.getRule();
        Optional<Rule.HystrixConfig> hystrixConfig =
                rule.getHystrixConfigs().stream().filter(c -> StringUtils.equals(c.getPath(),
                        gatewayContext.getRequest().getPath())).findFirst();
        return hystrixConfig;
    }

    /**
     * 正常异步路由逻辑
     *whenComplete方法:
     *
     * whenComplete是一个非异步的完成方法。
     * 当CompletableFuture的执行完成或者发生异常时，它提供了一个回调。
     * 这个回调将在CompletableFuture执行的相同线程中执行。这意味着，如果CompletableFuture的操作是阻塞的，那么回调也会在同一个阻塞的线程中执行。
     * 在这段代码中，如果whenComplete为true，则在future完成时使用whenComplete方法。这意味着complete方法将在future所在的线程中被调用。
     * whenCompleteAsync方法:
     *
     * whenCompleteAsync是异步的完成方法。
     * 它也提供了一个在CompletableFuture执行完成或者发生异常时执行的回调。
     * 与whenComplete不同，这个回调将在不同的线程中异步执行。通常情况下，它将在默认的ForkJoinPool中的某个线程上执行，除非提供了自定义的Executor。
     * 在代码中，如果whenComplete为false，则使用whenCompleteAsync。这意味着complete方法将在不同的线程中异步执行。
     * @param gatewayContext
     * @param hystrixConfig
     * @return
     */
    private CompletableFuture<Response> route(GatewayContext gatewayContext,
                                              Optional<Rule.HystrixConfig> hystrixConfig) {
        Request request = gatewayContext.getRequest().build();
        //执行具体的请求 并得到一个CompleatableFuture对象用于帮助我们执行后续的处理
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);
        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();
        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, gatewayContext, hystrixConfig);
            });
        } else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, gatewayContext, hystrixConfig);
            });
        }
        return future;
    }

    /**
     * 根据提供的GatewayContext和Hystrix配置，执行路由操作，并在熔断时执行降级逻辑。
     * 熔断会发生在：
     * 当 Hystrix 命令的执行时间超过配置的超时时间。
     * 当 Hystrix 命令的执行出现异常或错误。
     * 当连续请求失败率达到配置的阈值。
     * @param gatewayContext
     * @param hystrixConfig
     */
    private void routeWithHystrix(GatewayContext gatewayContext, Optional<Rule.HystrixConfig> hystrixConfig) {

        HystrixCommand.Setter setter =  //进行分组
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(gatewayContext.getUniqueId()))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(gatewayContext.getRequest().getPath()))
                        //线程池大小设置
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                .withCoreSize(hystrixConfig.get().getThreadCoreSize()))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                //线程池
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                //超时时间
                                .withExecutionTimeoutInMilliseconds(hystrixConfig.get().getTimeoutInMilliseconds())
                                .withExecutionIsolationThreadInterruptOnTimeout(true)
                                .withExecutionTimeoutEnabled(true));

        // 创建一个新的HystrixCommand对象，用于执行实际的路由操作。
        new HystrixCommand<Object>(setter) {
            @Override
            protected Object run() throws Exception {
                // 在Hystrix命令中执行路由操作，这是实际的业务逻辑。
                route(gatewayContext, hystrixConfig).get();
                return null;
            }

            @Override
            protected Object getFallback() {
                // 当熔断发生时，执行降级逻辑。
                // 检查是否是因为超时引发的熔断
                if (isFailedExecution() && getFailedExecutionException() instanceof TimeoutException) {
                    // 这里设置你自己的超时异常处理逻辑
                    // 例如，返回一个自定义的错误响应
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(
                            ResponseCode.GATEWAY_FALLBACK));
                } else {
                    // 其他类型的熔断处理
                    gatewayContext.setResponse(hystrixConfig.get().getFallbackResponse());
                }
                gatewayContext.written();
                return null;
            }
        }.execute(); // 执行Hystrix命令。
    }


    private void complete(Request request, Response response, Throwable throwable, GatewayContext gatewayContext,
                          Optional<Rule.HystrixConfig> hystrixConfig) {
        //请求已经处理完毕 释放请求资源
        gatewayContext.releaseRequest();
        //获取网关上下文规则
        Rule rule = gatewayContext.getRule();
        //获取请求重试次数
        int currentRetryTimes = gatewayContext.getCurrentRetryTimes();
        int confRetryTimes = rule.getRetryConfig().getTimes();
        //判断是否出现异常 如果是 进行重试
        if ((throwable instanceof TimeoutException || throwable instanceof IOException) &&
                currentRetryTimes <= confRetryTimes && !hystrixConfig.isPresent()) {
            //请求重试
            doRetry(gatewayContext, currentRetryTimes);
            return;
        }

        try {
            //之前出现了异常 执行异常返回逻辑
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable, gatewayContext.getUniqueId(), url,
                            ResponseCode.HTTP_RESPONSE_ERROR));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                //没有出现异常直接正常返回
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable t) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", t);
        } finally {
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);

            //增加日志记录
            accessLog.info("{} {} {} {} {} {} {}",
                    System.currentTimeMillis() - gatewayContext.getRequest().getBeginTime(),
                    gatewayContext.getRequest().getClientIp(),
                    gatewayContext.getRequest().getUniqueId(),
                    gatewayContext.getRequest().getMethod(),
                    gatewayContext.getRequest().getPath(),
                    gatewayContext.getResponse().getHttpResponseStatus().code(),
                    gatewayContext.getResponse().getFutureResponse().getResponseBodyAsBytes().length);

        }
    }


    private void doRetry(GatewayContext gatewayContext, int retryTimes) {
        System.out.println("当前重试次数为" + retryTimes);
        gatewayContext.setCurrentRetryTimes(retryTimes + 1);
        try {
            //调用路由过滤器方法再次进行请求重试
            doFilter(gatewayContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
