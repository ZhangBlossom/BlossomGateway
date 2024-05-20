package blossom.project.core.filter.loadbalance;

import blossom.project.core.DynamicConfigManager;
import blossom.project.common.config.ServiceInstance;
import blossom.project.common.exception.NotFoundException;
import blossom.project.core.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private final String serviceId;

    public ConsistentHashLoadBalanceRule(String serviceId) {
        this.serviceId = serviceId;
    }

    private static ConcurrentHashMap<String, ConsistentHashLoadBalanceRule>
            serviceMap = new ConcurrentHashMap<>();

    public static ConsistentHashLoadBalanceRule getInstance(String serviceId) {
        ConsistentHashLoadBalanceRule loadBalanceRule = serviceMap.get(serviceId);
        if (loadBalanceRule == null) {
            loadBalanceRule = new ConsistentHashLoadBalanceRule(serviceId);
            serviceMap.put(serviceId, loadBalanceRule);
        }
        return loadBalanceRule;
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx, boolean gray) {
        // 使用请求路径作为一致性哈希的键
        String path = ctx.getRequest().getPath();
        String uniqueId = ctx.getUniqueId();
        Set<ServiceInstance> serviceInstanceSet =
                DynamicConfigManager.getInstance().getServiceInstanceByUniqueId(uniqueId, gray);
        if (serviceInstanceSet.isEmpty()) {
            log.warn("No instance available for: {}", uniqueId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }

        // 构建节点列表
        List<String> nodes = serviceInstanceSet.stream()
                .map(ServiceInstance::getServiceInstanceId)
                .collect(Collectors.toList());

        // 创建一致性哈希环
        ConsistentHashing consistentHashing = new ConsistentHashing(nodes);

        // 根据键选择实例
        String selectedNode = consistentHashing.getNode(path);
        for (ServiceInstance instance : serviceInstanceSet) {
            if (instance.getServiceInstanceId().equals(selectedNode)) {
                return instance;
            }
        }

        // 如果未能选择实例，则返回第一个实例
        return serviceInstanceSet.iterator().next();
    }

    @Override
    @Deprecated
    public ServiceInstance choose(String path, boolean gray) {
        return null;
    }
}



class ConsistentHashing {
    // 虚拟节点的数量，用于增加哈希环的平衡性
    private static final int VIRTUAL_NODES = 10;

    // 哈希环
    private final SortedMap<Integer, String> hashCircle = new TreeMap<>();

    // 构造函数，初始化一致性哈希环
    public ConsistentHashing(List<String> nodes) {
        for (String node : nodes) {
            addNode(node);
        }
    }

    // 添加节点到哈希环
    private void addNode(String node) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            String virtualNode = node + "&&VN" + i;
            int hash = getHash(virtualNode);
            hashCircle.put(hash, virtualNode);
        }
    }

    // 根据键获取哈希环上最近的节点
    public String getNode(String key) {
        if (hashCircle.isEmpty()) {
            return null;
        }
        int hash = getHash(key);
        SortedMap<Integer, String> tailMap = hashCircle.tailMap(hash);
        Integer nodeHash = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
        return hashCircle.get(nodeHash);
    }

    // 获取字符串的哈希值
    private int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
}