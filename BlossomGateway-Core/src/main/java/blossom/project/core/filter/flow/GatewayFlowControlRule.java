package blossom.project.core.filter.flow;

import blossom.project.common.config.Rule;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/5 12:18
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * GatewayFlowControlRule接口
 * 网关流控规则接口
 */
public interface GatewayFlowControlRule {

    /**
     * 执行流控规则过滤器
     * @param flowControlConfig
     * @param serviceId
     */
    void doFlowControlFilter(Rule.FlowControlConfig flowControlConfig, String serviceId);
}
