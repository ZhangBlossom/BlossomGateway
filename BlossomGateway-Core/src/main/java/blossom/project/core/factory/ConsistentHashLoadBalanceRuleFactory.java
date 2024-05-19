package blossom.project.core.factory;

import blossom.project.core.filter.loadbalance.ConsistentHashLoadBalanceRule;

import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalanceRuleFactory {
    private static final ConcurrentHashMap<String, ConsistentHashLoadBalanceRule> ruleMap = new ConcurrentHashMap<>();

    private ConsistentHashLoadBalanceRuleFactory() {
        // 私有化构造函数，防止外部实例化
    }

    public static ConsistentHashLoadBalanceRule getInstance(String serviceId) {
        return ruleMap.computeIfAbsent(serviceId, ConsistentHashLoadBalanceRule::getInstance);
    }
}
