package blossom.project.core.filter;

import blossom.project.core.context.GatewayContext;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/30 9:48
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * FilterChainFactory接口
 * 过滤器链工厂 用于生成过滤器链
 */
public interface FilterChainFactory {

    /**
     * 构建过滤器链条
     * @param ctx
     * @return
     * @throws Exception
     */
    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

    /**
     * 通过过滤器ID获取过滤器
     * @param filterId
     * @return
     * @param <T>
     * @throws Exception
     */
    <T> T getFilterInfo(String filterId) throws Exception;

}
