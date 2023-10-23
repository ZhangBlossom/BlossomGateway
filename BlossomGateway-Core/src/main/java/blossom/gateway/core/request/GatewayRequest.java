package blossom.gateway.core.request;

import blossom.gateway.common.constant.BasicConstant;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.netty.buffer.ByteBuf;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;


import java.nio.charset.Charset;
import java.util.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 15:10
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * GatewayRequest类
 * 网关请求对象
 */
@Data
public class GatewayRequest implements IGatewayRequest {

    /**
     * 请求唯一id
     */
    private final String id;

    /**
     * 请求开始时间
     */
    private final long beginTime;

    /**
     * 请求结束时间
     */
    private final long endTime;

    /**
     * 字符集
     */
    private final Charset charset;

    /**
     * 客户端ip
     */
    private final String clientIp;


    /**
     * 服务端主机
     */
    private final String host;

    /**
     * 服务端的请求路径
     */
    private final String path;

    /**
     * uri
     */
    private final String uri;

    /**
     * 请求的格式
     */
    private final HttpMethod method;

    private final String contentType;

    /**
     * 请求头
     */
    private final HttpHeaders httpHeaders;


    /**
     * 参数解析器
     */
    private final QueryStringDecoder queryStringDecoder;


    /**
     * HTTP请求是否合法
     */
    private final FullHttpRequest fullHttpRequest;

    /**
     * 构建下游请求时的http请求构建器
     */
    private final RequestBuilder requestBuilder;

    /**
     * 请求体
     */
    private String body;


    /**
     * cookie
     */
    private Map<String, io.netty.handler.codec.http.cookie.Cookie> cookieMap;

    /**
     * post请求参数
     */
    private Map<String, List<String>> postParameters;

    /**
     * 可修改的scheme,默认为使用http协议
     */
    private String modifyScheme;

    /**
     * 可修改的host主机名
     */
    private String modifyHost;

    /**
     * 可修改的全球路径
     */
    private String modifyPath;

    public GatewayRequest(String id, long beginTime, long endTime, Charset charset, String clientIp, String host,
                          String path, String uri, HttpMethod method, HttpHeaders httpHeaders,
                          QueryStringDecoder queryStringDecoder, FullHttpRequest fullHttpRequest,
                          RequestBuilder requestBuilder, String contentType) {
        this.id = id;
        this.contentType = contentType;
        this.beginTime = System.currentTimeMillis();
        this.endTime = endTime;
        this.charset = charset;
        this.clientIp = clientIp;
        this.host = host;
        this.uri = uri;
        this.method = method;
        this.httpHeaders = httpHeaders;
        this.queryStringDecoder = new QueryStringDecoder(uri, charset);
        this.fullHttpRequest = fullHttpRequest;
        this.requestBuilder = new RequestBuilder();
        this.path = queryStringDecoder.path();

        this.modifyHost = host;
        this.modifyPath = path;
        this.modifyScheme = BasicConstant.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHttpHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());

        ByteBuf contentBuffer = fullHttpRequest.content();
        if (Objects.nonNull(contentBuffer)) {
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }

    }

    /**
     * 获取请求体body
     *
     * @return
     */
    public String getBody() {
        if (StringUtils.isEmpty(body)) {
            this.body = fullHttpRequest.content().toString(charset);
        }
        return this.body;
    }


    /**
     * 获取cookie
     *
     * @param name
     * @return
     */
    public Cookie getCookie(String name) {
        if (CollectionUtil.isEmpty(cookieMap)) {
            this.cookieMap = new HashMap<>(16);
            String cookieStr = getHttpHeaders().get(HttpHeaderNames.COOKIE);
            Set<io.netty.handler.codec.http.cookie.Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (io.netty.handler.codec.http.cookie.Cookie cookie : cookieSet) {
                cookieMap.put(name, cookie);
            }
        }
        return cookieMap.get(name);
    }

    public List<String> getQueryParameters(String name) {
        String body = getBody();
        //form表单
        if (formPost()) {
            if (postParameters == null) {
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body, false);
                postParameters = paramDecoder.parameters();
            }
            if (CollectionUtil.isEmpty(postParameters)) {
                return null;
            } else {
                return postParameters.get(name);
            }//json表单
        } else if (jsonPost()) {
            return Lists.newArrayList(JsonPath.read(body, name).toString());
        }
        return null;
    }

    /**
     * 判断是否是form表单
     *
     * @return
     */
    private boolean formPost() {
        return HttpMethod.POST.equals(method) && (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) || contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    /**
     * 判断是否是json
     *
     * @return
     */
    private boolean jsonPost() {
        return HttpMethod.POST.equals(method) && (contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString()));
    }

    @Override
    public void setModifyHost(String host) {
        this.modifyHost = host;
    }

    @Override
    public String getModifyHost() {
        return this.modifyHost;
    }

    @Override
    public void setModifyPath(String path) {
        this.modifyPath = path;
    }

    @Override
    public String getModifyPath() {
        return this.modifyPath;
    }

    @Override
    public void addHeader(String name, String value) {
        requestBuilder.addHeader(name, value);
    }

    @Override
    public void setHeader(String name, String value) {
        requestBuilder.setHeader(name, value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name, value);
    }

    @Override
    public void addBodyParam(String name, String value) {
        if (formPost()) {
            requestBuilder.addFormParam(name, value);
        }
    }

    @Override
    public void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    @Override
    public String getFinallyUrl() {
        return modifyScheme + modifyHost + modifyPath;
    }

    @Override
    public Request build() {
        requestBuilder.setUrl(getFinallyUrl());
        return requestBuilder.build();
    }
}
