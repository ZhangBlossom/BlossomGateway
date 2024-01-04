package blossom.project.core.filter.flow;

import blossom.project.common.config.Rule;
import blossom.project.common.enums.ResponseCode;
import blossom.project.common.exception.LimitedException;
import blossom.project.core.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static blossom.project.common.constant.FilterConst.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/5 12:19
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * FlowControlByPathRule类
 */
public class FlowControlByPathRule implements GatewayFlowControlRule {
    private String serviceId;

    private String path;

    private RedisCountLimiter redisCountLimiter;

    private static final String LIMIT_MESSAGE = "您的请求过于频繁,请稍后重试";

    public FlowControlByPathRule(String serviceId, String path, RedisCountLimiter redisCountLimiter) {
        this.serviceId = serviceId;
        this.path = path;
        this.redisCountLimiter = redisCountLimiter;
    }

    /**
     * 存放路径-流控规则的map
     */
    private static ConcurrentHashMap<String, FlowControlByPathRule> servicePathMap = new ConcurrentHashMap<>();

    /**
     * 通过服务id和路径获取具体的流控规则过滤器
     *
     * @param serviceId
     * @param path
     * @return
     */
    public static FlowControlByPathRule getInstance(String serviceId, String path) {
        StringBuffer buffer = new StringBuffer();
        String key = buffer.append(serviceId).append(".").append(path).toString();
        FlowControlByPathRule flowControlByPathRule = servicePathMap.get(key);
        //当前服务不存在限流规则 则保存之
        if (flowControlByPathRule == null) {
            flowControlByPathRule = new FlowControlByPathRule(serviceId, path, new RedisCountLimiter(new JedisUtil()));
            servicePathMap.put(key, flowControlByPathRule);
        }
        return flowControlByPathRule;
    }

    /**
     * 根据路径执行流控
     *
     * @param flowControlConfig
     * @param serviceId
     */
    @Override
    public void doFlowControlFilter(Rule.FlowControlConfig flowControlConfig, String serviceId) {
        if (flowControlConfig == null || StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(flowControlConfig.getConfig())) {
            return;
        }
        //获得当前路径对应的流控次数
        Map<String, Integer> configMap = JSON.parseObject(flowControlConfig.getConfig(), Map.class);
        //判断是否包含流控规则
        if (!configMap.containsKey(FLOW_CTL_LIMIT_DURATION) || !configMap.containsKey(FLOW_CTL_LIMIT_PERMITS)) {
            return;
        }
        //得到流控时间和时间内限制次数
        double duration = configMap.get(FLOW_CTL_LIMIT_DURATION);
        double permits = configMap.get(FLOW_CTL_LIMIT_PERMITS);
        StringBuffer buffer = new StringBuffer();
        //当前请求是否触发流控标志位
        boolean flag = false;
        String key = buffer.append(serviceId).append(".").append(path).toString();
        //如果是分布式项目 那么我们就需要使用Redis来实现流控  单机则可以直接使用Guava
        if (FLOW_CTL_MODEL_DISTRIBUTED.equalsIgnoreCase(flowControlConfig.getModel())) {
            flag = redisCountLimiter.doFlowControl(key, (int) permits, (int) duration);
        } else {
            //单机版限流 直接用Guava
            GuavaCountLimiter guavaCountLimiter = GuavaCountLimiter.getInstance(serviceId, flowControlConfig);
            if (guavaCountLimiter == null) {
                throw new RuntimeException("获取单机限流工具类为空");
            }
            double count = Math.ceil(permits / duration);
            flag = guavaCountLimiter.acquire((int) count);
        }
        if (!flag) {
            throw new LimitedException(ResponseCode.FLOW_CONTROL_ERROR);
        }
    }
}
