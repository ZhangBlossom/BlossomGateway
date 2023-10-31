package blossom.project.core.filter;

import blossom.project.core.context.GatewayContext;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 9:43
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Filter接口  过滤器顶层接口
 */
public interface Filter {
    void doFilter(GatewayContext ctx) throws  Exception;

    default int getOrder(){
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if(annotation != null){
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    };
}
