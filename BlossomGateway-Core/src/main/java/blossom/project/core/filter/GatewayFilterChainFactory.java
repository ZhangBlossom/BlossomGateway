package blossom.project.core.filter;

import blossom.project.common.config.Rule;
import blossom.project.core.context.GatewayContext;
import blossom.project.core.filter.router.RouterFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/30 9:48
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * <p>
 * 过滤器工厂具体实现
 * 用于构造过滤器链条
 */
@Slf4j
public class GatewayFilterChainFactory implements FilterFactory {

    private static class SingletonInstance {
        private static final GatewayFilterChainFactory INSTANCE = new GatewayFilterChainFactory();
    }

    /**
     * 饿汉式获取单例
     * @return
     */
    public static GatewayFilterChainFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private Map<String, Filter> processorFilterIdMap = new ConcurrentHashMap<>();

    /**
     * 通过ServiceLoader的方式添加我们实现的过滤器类
     * 将其保存到系统缓存中
     */
    public GatewayFilterChainFactory() {
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        serviceLoader.stream().forEach(filterProvider -> {
            Filter filter = filterProvider.get();
            FilterAspect annotation = filter.getClass().getAnnotation(FilterAspect.class);
            log.info("load filter success:{},{},{},{}", filter.getClass(),
                    annotation.id(), annotation.name(), annotation.order());
            if (annotation != null) {
                //添加到过滤集合
                String filterId = annotation.id();
                if (StringUtils.isEmpty(filterId)) {
                    filterId = filter.getClass().getName();
                }
                processorFilterIdMap.put(filterId, filter);
            }
        });

    }

    //测试功能是否成功
    public static void main(String[] args) {
        new GatewayFilterChainFactory();
    }


    /**
     * 对网关请求上下文
     * 通过配置中心选定对应的配置
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception {
        GatewayFilterChain chain = new GatewayFilterChain();
        List<Filter> filters = new ArrayList<>();
        //获取过滤器配置规则  是我们再配置中心进行配置的
        //这是由于我们的过滤器链是由我们的规则定义的
        Rule rule = ctx.getRule();
        if (rule != null) {
            //获取所有的过滤器
            Set<Rule.FilterConfig> filterConfigs = rule.getFilterConfigs();
            Iterator iterator = filterConfigs.iterator();
            Rule.FilterConfig filterConfig;
            while (iterator.hasNext()) {
                filterConfig = (Rule.FilterConfig) iterator.next();
                if (filterConfig == null) {
                    continue;
                }
                String filterId = filterConfig.getId();
                if (StringUtils.isNotEmpty(filterId) && getFilterInfo(filterId) != null) {
                    Filter filter = getFilterInfo(filterId);
                    filters.add(filter);
                }
            }
        }
        //添加路由过滤器-因为我们的网关最后要执行的就是路由转发
        filters.add(new RouterFilter());
        //排序
        filters.sort(Comparator.comparingInt(Filter::getOrder));
        //添加到链表中
        chain.addFilterList(filters);
        return chain;
    }

    @Override
    public Filter getFilterInfo(String filterId) throws Exception {
        return processorFilterIdMap.get(filterId);
    }
}
