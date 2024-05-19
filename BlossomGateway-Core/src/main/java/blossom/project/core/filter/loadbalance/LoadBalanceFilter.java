package blossom.project.core.filter.loadbalance;

import blossom.project.common.config.Rule;
import blossom.project.common.config.ServiceInstance;
import blossom.project.common.exception.NotFoundException;
import blossom.project.core.context.GatewayContext;
import blossom.project.core.factory.ConsistentHashLoadBalanceRuleFactory;
import blossom.project.core.filter.Filter;
import blossom.project.core.filter.FilterAspect;
import blossom.project.core.request.GatewayRequest;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static blossom.project.common.constant.FilterConst.*;
import static blossom.project.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 9:53
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * LoadBalanceFilter类
 */
@Slf4j
@FilterAspect(id = LOAD_BALANCE_FILTER_ID, name = LOAD_BALANCE_FILTER_NAME, order = LOAD_BALANCE_FILTER_ORDER)
public class LoadBalanceFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) {
        //拿到服务id
        String serviceId = ctx.getUniqueId();
        //从请求上下文中获取负载均衡策略
        LoadBalanceGatewayRule gatewayLoadBalanceRule = getLoadBalanceRule(ctx);
        //获取某一台服务实例
        ServiceInstance serviceInstance = gatewayLoadBalanceRule.choose(ctx, ctx.isGray());
        System.out.println("IP为" + serviceInstance.getIp() + ",端口号：" + serviceInstance.getPort());
        GatewayRequest request = ctx.getRequest();
        if (serviceInstance != null && request != null) {
            String host = serviceInstance.getIp() + ":" + serviceInstance.getPort();
            request.setModifyHost(host);
        } else {
            log.warn("No instance available for :{}", serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
    }


    /**
     * 根据配置获取负载均衡器
     *
     * @param ctx
     * @return
     */
    public LoadBalanceGatewayRule getLoadBalanceRule(GatewayContext ctx) {
        LoadBalanceGatewayRule loadBalanceRule = null;
        Rule configRule = ctx.getRule();
        if (configRule != null) {
            Set<Rule.FilterConfig> filterConfigs = configRule.getFilterConfigs();
            Iterator iterator = filterConfigs.iterator();
            Rule.FilterConfig filterConfig;
            while (iterator.hasNext()) {
                filterConfig = (Rule.FilterConfig) iterator.next();
                if (filterConfig == null) {
                    continue;
                }
                String filterId = filterConfig.getId();
                if (LOAD_BALANCE_FILTER_ID.equals(filterId)) {
                    String config = filterConfig.getConfig();
                    //默认选择权重负载均衡过滤器
                    String strategy = LOAD_BALANCE_STRATEGY_WEIGHT;
                    if (StringUtils.isNotEmpty(config)) {
                        Map<String, String> mapTypeMap = JSON.parseObject(config, Map.class);
                        strategy = mapTypeMap.getOrDefault(LOAD_BALANCE_KEY, strategy);
                    }
                    switch (strategy) {
                        case LOAD_BALANCE_STRATEGY_RANDOM:
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        case LOAD_BALANCE_STRATEGY_ROUND_ROBIN:
                            loadBalanceRule = RoundRobinLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        case LOAD_BALANCE_STRATEGY_WEIGHT:
                            loadBalanceRule = WeightLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        case LOAD_BALANCE_STRATEGY_CONSISTENT_HASH:
                            loadBalanceRule =
                                    ConsistentHashLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        default:
                            log.warn("No loadBalance strategy for service:{}", strategy);
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                    }
                }
            }
        }
        return loadBalanceRule;
    }
}

