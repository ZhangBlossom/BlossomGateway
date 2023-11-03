package blossom.project.core.netty.processor;

import blossom.project.common.enums.ResponseCode;
import blossom.project.common.exception.BaseException;
import blossom.project.common.exception.ConnectException;
import blossom.project.common.exception.ResponseException;
import blossom.project.core.ConfigLoader;
import blossom.project.core.context.GatewayContext;
import blossom.project.core.context.HttpRequestWrapper;
import blossom.project.core.filter.FilterFactory;
import blossom.project.core.filter.GatewayFilterChainFactory;
import blossom.project.core.helper.AsyncHttpHelper;
import blossom.project.core.helper.RequestHelper;
import blossom.project.core.helper.ResponseHelper;
import blossom.project.core.response.GatewayResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;


import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    private FilterFactory filterFactory = GatewayFilterChainFactory.getInstance();

    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();

        try {
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);
            //执行过滤器逻辑
            filterFactory.buildFilterChain(gatewayContext).doFilter(gatewayContext);
        } catch (BaseException e) {
            log.error("process error {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            log.error("process unkown error", t);
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }

    }

    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE); //释放资源后关闭channel
        ReferenceCountUtil.release(request);
    }

}
