package blossom.gateway.core.context;

import blossom.gateway.core.request.GatewayRequest;
import blossom.gateway.core.response.GatewayResponse;
import blossom.gateway.core.rule.Rule;
import cn.hutool.core.lang.Assert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 15:09
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * GatewayContext类
 */
public class GatewayContext extends BaseContext {

    public GatewayRequest request;

    public GatewayResponse response;

    public Rule rule;

    public GatewayContext(String protocol, ChannelHandlerContext nettyContext, boolean keepAlive,
                          GatewayRequest request, Rule rule) {
        super(protocol, nettyContext, keepAlive);
        this.request = request;
        this.rule = rule;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayContextBuilder {
        private String protocol;
        private ChannelHandlerContext nettyContext;
        private GatewayRequest request;
        private Rule rule;
        private boolean keepAlive;

        public GatewayContextBuilder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public GatewayContextBuilder setNettyContext(ChannelHandlerContext nettyContext) {
            this.nettyContext = nettyContext;
            return this;
        }

        public GatewayContextBuilder setRequest(GatewayRequest request) {
            this.request = request;
            return this;
        }

        public GatewayContextBuilder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public GatewayContext build() {
            Assert.notNull(protocol, "protocal can not be null!");
            Assert.notNull(nettyContext, "nettyContext can not be null!");
            Assert.notNull(request, "request can not be null!");
            Assert.notNull(rule, "rule can not be null!");
            return new GatewayContext(protocol, nettyContext, keepAlive, request, rule);
        }
    }


    /**
     * 获取必要上下文参数
     * @param key
     * @return
     */
    public Object  getRequireAttribute(String key){
        Object value = getAttribute(key);
        Assert.notNull(value,"lack of necessary parameters!");
        return value;
    }

    /**
     * 获取指定key的上下文参数 不存在返回默认值
     * @param key
     * @param defaultVal
     * @return
     */
    public Object getAttributeOrDefault(String key,Object defaultVal){
        return attributes.getOrDefault(key,defaultVal);
    }


    /**
     * 获取指定的过滤器信息
     * @param id
     * @return
     */
    public Rule.FilterConfig getFilterConfig(String id){
        return rule.getFilterConfig(id);
    }

    /**
     * 获取请求id
     * @return
     */
    public String getRequestID(){
        return request.getId();
    }

    /**
     * 释放资源
     */
    @Override
    public void releaseRequest(){
        if (requestReleased.compareAndExchange(false,true)){
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
    }

    /**
     * 获取原始请求对象
     * @return
     */
    public GatewayRequest getOriginRequest(){
        return request;
    }

    @Override
    public GatewayRequest getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = (GatewayRequest) request;
    }

    @Override
    public GatewayResponse getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = (GatewayResponse) response;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
