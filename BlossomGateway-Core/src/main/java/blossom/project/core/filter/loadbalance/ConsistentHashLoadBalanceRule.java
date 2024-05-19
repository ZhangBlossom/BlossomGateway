package blossom.project.core.filter.loadbalance;

import blossom.project.common.config.DynamicConfigManager;
import blossom.project.common.config.ServiceInstance;
import blossom.project.common.exception.NotFoundException;
import blossom.project.core.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static blossom.project.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;


/**
 * @author: ZhangBlossom
 * @date: 2024/5/19 9:50
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ConsistentHashLoadBalanceRule提供一致性hash算法的负载均衡实现
 *
 * 参考资料：
 * https://www.xiaolincoding.com/os/8_network_system/hash.html
 */

@Slf4j
public class ConsistentHashLoadBalanceRule implements LoadBalanceGatewayRule {

    // 虚拟节点的数量
    private static final int VIRTUAL_NODE_COUNT = 100;

    // 服务器与其对应的虚拟节点映射
    private final ConcurrentHashMap<String /* uniqueId */, TreeMap<Integer, ServiceInstance>> virtualNodeMap = new ConcurrentHashMap<>();

    // 服务器列表
    private final List<String> serverList = new ArrayList<>();

    public ConsistentHashLoadBalanceRule(String serviceId) {
        Set<ServiceInstance> serviceInstanceSet = DynamicConfigManager.getInstance().getAllServiceInstances(serviceId);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("No instance available for: {}", serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
        for (ServiceInstance instance : serviceInstanceSet) {
            addServer(instance);
        }
    }

    // 添加服务器及其对应的虚拟节点
    private void addServer(ServiceInstance instance) {
        String server = instance.getServiceId();
        serverList.add(server);
        for (int i = 0; i < VIRTUAL_NODE_COUNT; i++) {
            String virtualNode = server + "&&VN" + i;
            int hash = getHash(virtualNode);
            TreeMap<Integer, ServiceInstance> nodeMap = virtualNodeMap.getOrDefault(server, new TreeMap<>());
            nodeMap.put(hash, instance);
            virtualNodeMap.put(server, nodeMap);
        }
    }

    // 获取哈希值
    private int getHash(String key) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < key.length(); i++)
            hash = (hash ^ key.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return Math.abs(hash);
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx, boolean gray) {
        return choose(ctx.getUniqueId(), gray);
    }

    @Override
    public ServiceInstance choose(String serviceId, boolean gray) {
        TreeMap<Integer, ServiceInstance> nodeMap = virtualNodeMap.get(serviceId);
        if (nodeMap == null || nodeMap.isEmpty()) {
            log.warn("No instance available for: {}", serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
        int hash = getHash(ctx.getUniqueId());
        Map.Entry<Integer, ServiceInstance> entry = nodeMap.ceilingEntry(hash);
        if (entry == null) {
            entry = nodeMap.firstEntry();
        }
        return entry.getValue();
    }
}