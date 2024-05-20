package blossom.project.core.request;

import blossom.project.common.constant.BasicConst;
import blossom.project.common.utils.TimeUtil;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;


import java.nio.charset.Charset;
import java.util.*;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 网关请求类
 */
@Slf4j
public class GatewayRequest implements IGatewayRequest{

    /**
     * 服务ID
     */
    @Getter
    private final String uniqueId;

    /**
     * 请求进入网关时间
     */
    @Getter
    private final long beginTime;

    /**
     * 字符集不会变的
     */
    @Getter
    private final  Charset charset;

    /**
     * 客户端的IP，主要用于做流控、黑白名单
     */
    @Getter
    private final String clientIp;

    /**
     * 请求的地址：IP：port
     */
    @Getter
    private final String host;

    /**
     *  请求的路径   /XXX/XXX/XX
     */
    @Getter
    private final String path;

    /**
     * URI：统一资源标识符，/XXX/XXX/XXX?attr1=value&attr2=value2
     * URL：统一资源定位符，它只是URI的子集一个实现
     */
    @Getter
    private final String uri;

    /**
     * 请求方法 post/put/GET
     */
    @Getter
    private final HttpMethod method;

    /**
     * 请求的格式
     */
    @Getter
    private final String contentType;

    /**
     * 请求头信息
     */
    @Getter
    private final HttpHeaders headers;

    /**
     * 参数解析器
     */
    @Getter
    private final QueryStringDecoder queryStringDecoder;

    /**
     * FullHttpRequest
     */
    @Getter
    private final FullHttpRequest fullHttpRequest;

    /**
     * 请求体
     */
    @Getter
    private String body;


    @Setter
    @Getter
    private long userId;

    /**
     * 请求Cookie
     */
    @Getter
    private Map<String,io.netty.handler.codec.http.cookie.Cookie> cookieMap;

    /**
     * post请求定义的参数结合
     */
    @Getter
    private Map<String,List<String>> postParameters;


    /******可修改的请求变量***************************************/
    /**
     * 可修改的Scheme，默认是http://
     */
    private String modifyScheme;

    private String modifyHost;

    private String modifyPath;

    /**
     * 构建下游请求是的http请求构建器
     */
    private final RequestBuilder requestBuilder;

    /**
     * 构造器
     * @param uniqueId
     * @param charset
     * @param clientIp
     * @param host
     * @param uri
     * @param method
     * @param contentType
     * @param headers
     * @param fullHttpRequest
     */
    public GatewayRequest(String uniqueId, Charset charset, String clientIp, String host, String uri, HttpMethod method, String contentType, HttpHeaders headers, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;
        this.beginTime = TimeUtil.currentTimeMillis();
        this.charset = charset;
        this.clientIp = clientIp;
        this.host = host;
        this.uri = uri;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.fullHttpRequest = fullHttpRequest;
        this.queryStringDecoder = new QueryStringDecoder(uri,charset);
        this.path  = queryStringDecoder.path();
        this.modifyHost = host;
        this.modifyPath = path;

        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder = new RequestBuilder();
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());

        ByteBuf contentBuffer = fullHttpRequest.content();
        if(Objects.nonNull(contentBuffer)){
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }

    /**
     * 获取请求体
     * @return
     */
    public String getBody(){
        if(StringUtils.isEmpty(body)){
            body = fullHttpRequest.content().toString(charset);
        }
        return body;
    }

    /**
     * 获取Cookie
     * @param name
     * @return
     */
    public  io.netty.handler.codec.http.cookie.Cookie getCookie(String name){
        if(cookieMap == null){
            cookieMap = new HashMap<String,io.netty.handler.codec.http.cookie.Cookie>();
            String cookieStr = getHeaders().get(HttpHeaderNames.COOKIE);
            if (StringUtils.isBlank(cookieStr)){
                return null;
            }
            Set<io.netty.handler.codec.http.cookie.Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for(io.netty.handler.codec.http.cookie.Cookie cookie: cookies){
                cookieMap.put(name,cookie);
            }
        }
        return cookieMap.get(name);
    }

    /**
     * 获取指定名词参数值
     * @param name
     * @return
     */
    public List<String> getQueryParametersMultiple(String name){
        return  queryStringDecoder.parameters().get(name);
    }

    /**
     * post请求获取指定名词参数值
     * @param name
     * @return
     */
    public List<String> getPostParametersMultiples(String name){
        String body = getBody();
        if(isFormPost()){
            if(postParameters == null){
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body,false);
                postParameters = paramDecoder.parameters();
            }
            if(postParameters == null || postParameters.isEmpty()){
                return null;
            }else{
                return   postParameters.get(name);
            }
        } else if (isJsonPost()){
            try {
                return Lists.newArrayList(JsonPath.read(body,name).toString());
            }catch (Exception e){
                log.error("JsonPath解析失败，JsonPath:{},Body:{},",name,body,e);
            }
        }
        return null;
    }



    @Override
    public void setModifyHost(String modifyHost) {
        this.modifyHost = modifyHost;
    }

    @Override
    public String getModifyHost() {
        return modifyHost;
    }

    @Override
    public void setModifyPath(String modifyPath) {
        this.modifyPath = modifyPath;
    }

    @Override
    public String getModifyPath() {
        return modifyPath;
    }

    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name,value);
    }

    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name,value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name,value);
    }

    @Override
    public void addFormParam(String name, String value) {
        if(isFormPost()){
            requestBuilder.addFormParam(name,value);
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
    public String getFinalUrl() {
        return modifyScheme+modifyHost+modifyPath;
    }

    @Override
    public Request build() {
        requestBuilder.setUrl(getFinalUrl());
        //设置用户id 用于下游的服务使用
        requestBuilder.addHeader("userId", String.valueOf(userId));
        return requestBuilder.build();
    }

    public  boolean isFormPost(){
        return HttpMethod.POST.equals(method) &&
                (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) ||
                        contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    public  boolean isJsonPost(){
        return HttpMethod.POST.equals(method) && contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }



}
