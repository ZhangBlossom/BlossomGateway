package blossom.gateway.core.response;

import blossom.gateway.common.enums.ResponseCode;
import blossom.gateway.common.response.DisplayResponse;
import com.alibaba.fastjson2.JSON;
import io.netty.handler.codec.http.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asynchttpclient.Response;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 15:11
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * GatewayResponse类
 */
@Data
@NoArgsConstructor
public class GatewayResponse {

    /**
     * 响应头
     */
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();


    /**
     * 额外的响应头信息
     */
    private HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    /**
     * 响应内容
     */
    private String content;

    /**
     * 响应状态码
     */
    private HttpResponseStatus httpResponseStatus;

    /**
     * 异步返回对象
     */
    private Response futureResponse;

    /**
     * 设置响应头信息
     *
     * @param key
     * @param value
     */
    public void setHeader(CharSequence key, CharSequence value) {
        responseHeaders.add(key, value);
    }


    /**
     * 构建异步响应对象
     *
     * @param futureResponse 异步对象参数
     * @return
     */
    public static GatewayResponse buildFutureGatewayResponse(Response futureResponse) {
        GatewayResponse response = new GatewayResponse();
        response.setFutureResponse(futureResponse);
        response.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return response;
    }


    /**
     * 返回一个json类型的响应信息,失败时使用
     *
     * @param code 失败的时候是有code的
     * @param args
     * @return
     */
    public static GatewayResponse buildFutureGatewayResponse(ResponseCode code, Object... args) {
        DisplayResponse displayResponse = new DisplayResponse();
        displayResponse.setCode(code.getCode());
        displayResponse.setData(JSON.toJSONString(args));
        displayResponse.setMessage(code.getMessage());
        displayResponse.setStatus(code.getStatus());

        String content = JSON.toJSONString(displayResponse);
        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(code.getStatus());
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.setContent(content);

        return response;
    }


    /**
     * 返回一个json类型的响应信息,成功的时候使用
     * 成功的时候是没有code的,直接200
     *
     * @param args
     * @return
     */
    public static GatewayResponse buildFutureGatewayResponse(Object... args) {
        DisplayResponse displayResponse = new DisplayResponse();
        displayResponse.setCode(ResponseCode.SUCCESS.getCode());
        displayResponse.setData(JSON.toJSONString(args));
        displayResponse.setMessage(ResponseCode.SUCCESS.getMessage());
        displayResponse.setStatus(ResponseCode.SUCCESS.getStatus());
        String content = JSON.toJSONString(displayResponse);

        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.setContent(content);

        return response;
    }

}
