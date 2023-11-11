package blossom.project.core.netty.processor;

import blossom.project.common.enums.ResponseCode;
import blossom.project.core.Config;
import blossom.project.core.context.HttpRequestWrapper;
import blossom.project.core.disruptor.EventListener;
import blossom.project.core.disruptor.ParallelQueueHandler;
import blossom.project.core.helper.ResponseHelper;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
/**
 * @author: ZhangBlossom
 * @date: 2023/11/13 23:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 *
 */
@Slf4j
public class DisruptorNettyCoreProcessor implements NettyProcessor {
    /**
     * 线程前缀
     */
    private static  final String THREAD_NAME_PREFIX = "gateway-queue-";

    private Config config;

    /**
     * Disruptor只是缓存依旧需要用到Netty核心处理器
     */

    private NettyCoreProcessor nettyCoreProcessor;
    /**
     * 处理类
     */
    private ParallelQueueHandler<HttpRequestWrapper> parallelQueueHandler;

    public DisruptorNettyCoreProcessor(Config config, NettyCoreProcessor nettyCoreProcessor) {
        this.config = config;
        this.nettyCoreProcessor = nettyCoreProcessor;
        ParallelQueueHandler.Builder<HttpRequestWrapper> builder = new ParallelQueueHandler.Builder<HttpRequestWrapper>()
                .setBufferSize(config.getBufferSize())
                .setThreads(config.getProcessThread())
                .setProducerType(ProducerType.MULTI)
                .setNamePrefix(THREAD_NAME_PREFIX)
                .setWaitStrategy(config.getWaitStrategy());
        //监听事件处理类
        BatchEventListenerProcessor batchEventListenerProcessor = new BatchEventListenerProcessor();
        builder.setListener(batchEventListenerProcessor);
        this.parallelQueueHandler = builder.build();

    }

    @Override
    public void process(HttpRequestWrapper wrapper) {
        this.parallelQueueHandler.add(wrapper);
    }


    /**
     * 监听处理类
     */
    public class BatchEventListenerProcessor implements EventListener<HttpRequestWrapper> {

        @Override
        public void onEvent(HttpRequestWrapper event) {
            nettyCoreProcessor.process(event);

        }

        @Override
        public void onException(Throwable ex, long sequence, HttpRequestWrapper event) {
            HttpRequest request = event.getRequest();
            ChannelHandlerContext ctx = event.getCtx();
            try {
                log.error("BatchEventListenerProcessor onException请求写回失败，request:{},errMsg:{} ",request,ex.getMessage(),ex);

                //构建响应对象
                FullHttpResponse fullHttpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
                if(!HttpUtil.isKeepAlive(request)){
                    ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                }else{
                    fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(fullHttpResponse);
                }
            }catch (Exception e){
                log.error("BatchEventListenerProcessor onException请求写回失败，request:{},errMsg:{} ",request,e.getMessage(),e);
            }
        }
    }


    @Override
    public void start() {
        parallelQueueHandler.start();
    }

    @Override
    public void shutDown() {
        parallelQueueHandler.shutDown();
    }



}
