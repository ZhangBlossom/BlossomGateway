package blossom.project.core.helper;

import blossom.gateway.common.constant.BasicConst;
import blossom.gateway.common.enums.ResponseCode;
import blossom.project.core.context.IContext;
import blossom.project.core.response.GatewayResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;


import java.util.Objects;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Test类
 * 响应的辅助类
 */
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
					.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
			} 
			//	长连接：
			else {
				httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				context.getNettyCtx().writeAndFlush(httpResponse);
			}
			//	2:	设置写回结束状态为： COMPLETED
			context.completed();
		}
		else if(context.isCompleted()){
			context.invokeCompletedCallBack();
		}
		
	}
	
}
