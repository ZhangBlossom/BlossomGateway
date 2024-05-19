package blossom.project.core.helper;

import blossom.project.common.constant.BasicConst;
import blossom.project.common.enums.ResponseCode;
import blossom.project.core.context.IContext;
import blossom.project.core.response.GatewayResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;


import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 响应的辅助类
 */

@Slf4j
public class ResponseHelper {

	/**
	 * 获取响应对象
	 */
	public static FullHttpResponse getHttpResponse(ResponseCode responseCode) {
		GatewayResponse gatewayResponse = GatewayResponse.buildGatewayResponse(responseCode);
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
				HttpResponseStatus.INTERNAL_SERVER_ERROR,
				Unpooled.wrappedBuffer(gatewayResponse.getContent().getBytes()));
		
		httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
		httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
		return httpResponse;
	}
	
	/**
	 * 通过上下文对象和Response对象 构建FullHttpResponse
	 */
	private static FullHttpResponse getHttpResponse(IContext ctx, GatewayResponse gatewayResponse) {
		ByteBuf content;
		if(Objects.nonNull(gatewayResponse.getFutureResponse())) {
			content = Unpooled.wrappedBuffer(gatewayResponse.getFutureResponse()
					.getResponseBodyAsByteBuffer());
		}
		else if(gatewayResponse.getContent() != null) {
			content = Unpooled.wrappedBuffer(gatewayResponse.getContent().getBytes());
		}
		else {
			content = Unpooled.wrappedBuffer(BasicConst.BLANK_SEPARATOR_1.getBytes());
		}
		
		if(Objects.isNull(gatewayResponse.getFutureResponse())) {
			DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
					gatewayResponse.getHttpResponseStatus(),
					 content);	
			httpResponse.headers().add(gatewayResponse.getResponseHeaders());
			httpResponse.headers().add(gatewayResponse.getExtraResponseHeaders());
			httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
			return httpResponse;
		} else {
			gatewayResponse.getFutureResponse().getHeaders().add(gatewayResponse.getExtraResponseHeaders());
			
			DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
					 HttpResponseStatus.valueOf(gatewayResponse.getFutureResponse().getStatusCode()),
					 content);	
			httpResponse.headers().add(gatewayResponse.getFutureResponse().getHeaders());
			return httpResponse;
		}
	}

	private static final double DEFAULT_ALPHA = 0.2; // 默认的 alpha 值

	// 发送成功的数据包数量
	private static final AtomicInteger successCount = new AtomicInteger(0);
	// 发送失败的数据包数量
	private static final AtomicInteger failureCount = new AtomicInteger(0);

	private static double lossRate = 0.0; // 初始丢包率
	private static double alpha = DEFAULT_ALPHA; // 指数加权移动平均的平滑因子

	/**
	 * 写回响应信息方法
	 */
	public static void writeResponse(IContext context) {
		
		//	释放资源
		context.releaseRequest();
		
		if(context.isWritten()) {
			//	1：第一步构建响应对象，并写回数据
			FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(context, (GatewayResponse)context.getResponse());
			if(!context.isKeepAlive()) {
				context.getNettyCtx()
					.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE)
						.addListener((ChannelFutureListener) future1 -> {
					if (future1.isSuccess()) {
						// 发送成功
						successCount.incrementAndGet();
					} else {
						// 发送失败
						failureCount.incrementAndGet();
					}
				});

			} 
			//	长连接：
			else {
				httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				context.getNettyCtx().writeAndFlush(httpResponse).addListener((ChannelFutureListener) future1 -> {
					if (future1.isSuccess() &&
							System.currentTimeMillis()%10 < 7) {
						// 发送成功
						successCount.incrementAndGet();
					} else {
						// 发送失败
						failureCount.incrementAndGet();
					}
				});

			}
			//	2:	设置写回结束状态为： COMPLETED
			context.completed();

			// 计算丢包率
			updateLossRate();
			log.info("当前丢包率为: {}", lossRate);
		}
		else if(context.isCompleted()){
			context.invokeCompletedCallBack();
		}
		
	}

	/**
	 * 使用指数加权移动平均（Exponential Weighted Moving Average，EWMA）算法。
	 * 这是一种在时间序列数据中广泛应用的算法，它对最近的观测值赋予更高的权重，
	 * 并对较早的观测值赋予较低的权重。这样，就可以更准确地反映出丢包率的变化趋势，
	 * 而不仅仅是简单地计算成功和失败的比例。
	 *
	 * 更新丢包率
	 */
	private static void updateLossRate() {
		int success = successCount.get();
		int failure = failureCount.get();
		int total = success + failure;
		if (total != 0) {
			double newLossRate = alpha * failure / total;
			lossRate = alpha * newLossRate + (1 - alpha) * lossRate;
		}
	}
}
