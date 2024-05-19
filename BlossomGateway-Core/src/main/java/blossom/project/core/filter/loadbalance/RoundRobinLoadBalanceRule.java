package blossom.project.core.filter.loadbalance;


import blossom.project.core.DynamicConfigManager;
import blossom.project.common.config.ServiceInstance;
import blossom.project.common.exception.NotFoundException;
import blossom.project.core.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static blossom.project.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 19:21
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RoundRobinLoadBalanceRule提供轮询负载均衡算法实现
 */
@Slf4j
public class RoundRobinLoadBalanceRule implements LoadBalanceGatewayRule {

    private AtomicInteger position = new AtomicInteger(1);

    private final String serviceId;


    public RoundRobinLoadBalanceRule(String serviceId) {
        this.serviceId = serviceId;
    }

    private static ConcurrentHashMap<String, RoundRobinLoadBalanceRule> serviceMap = new ConcurrentHashMap<>();

    public static RoundRobinLoadBalanceRule getInstance(String serviceId) {
        RoundRobinLoadBalanceRule loadBalanceRule = serviceMap.get(serviceId);
        if (loadBalanceRule == null) {
            loadBalanceRule = new RoundRobinLoadBalanceRule(serviceId);
            serviceMap.put(serviceId, loadBalanceRule);
        }
        return loadBalanceRule;
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx,boolean gray) {
        return choose(ctx.getUniqueId(),gray);
    }

    @Override
    public ServiceInstance choose(String serviceId,boolean gray) {
        Set<ServiceInstance> serviceInstanceSet =
                DynamicConfigManager.getInstance().getServiceInstanceByUniqueId(serviceId,gray);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("No instance available for:{}", serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
        List<ServiceInstance> instances = new ArrayList<ServiceInstance>(serviceInstanceSet);
        if (instances.isEmpty()) {
            log.warn("No instance available for service:{}", serviceId);
            return null;
        } else {
            int pos = Math.abs(this.position.incrementAndGet());
            return instances.get(pos % instances.size());
        }
    }
}
