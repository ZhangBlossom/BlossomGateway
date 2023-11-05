package blossom.project.core.filter.flow;

import blossom.project.common.config.Rule;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static blossom.project.common.constant.FilterConst.FLOW_CTL_LIMIT_DURATION;
import static blossom.project.common.constant.FilterConst.FLOW_CTL_LIMIT_PERMITS;


/**
 * @author: ZhangBlossom
 * @date: 2023/11/5 17:18
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * GuavaCountLimiter接口
 */
public class GuavaCountLimiter {

    /**
     * guava限流根据
     */
    private RateLimiter rateLimiter;
    /**
     * 最大请求数量
     */
    private double maxPermits;

    /**
     * 没有预热，尽可能按照这个速率来分发许可。速率限制器不会考虑之前的请求，也不会允许短时间内的请求速率超过指定的速率。
     * 这可能导致一些请求需要等待，以便速率限制器可以分发足够的许可。
     *
     * @param maxPermits
     */

    public GuavaCountLimiter(double maxPermits) {
        this.maxPermits = maxPermits;
        rateLimiter = RateLimiter.create(maxPermits);
    }

    /**
     * 创建一个具有预热期的速率限制器。预热期是指在速率限制器刚开始使用时，速率限制器允许请求超过其平均速率。
     * 预热结束后按照指定的速度分发许可。
     * 提供预热意味着可以在应用启动或负载增加时，允许一些瞬时的高请求速率，然后逐渐调整到稳定的速率。
     *
     * @param maxPermits           表示每秒的最大许可数量
     * @param warmUpPeriodAsSecond 预热时长
     */
    public GuavaCountLimiter(double maxPermits, long warmUpPeriodAsSecond) {
        this.maxPermits = maxPermits;
        rateLimiter = RateLimiter.create(maxPermits, warmUpPeriodAsSecond, TimeUnit.SECONDS);
    }

    /**
     * 路径 - 限流器
     */
    public static ConcurrentHashMap<String, GuavaCountLimiter> resourceRateLimiterMap = new ConcurrentHashMap<String,
            GuavaCountLimiter>();

    public static GuavaCountLimiter getInstance(String serviceId, Rule.FlowControlConfig flowControlConfig) {
        if (StringUtils.isEmpty(serviceId) || flowControlConfig == null || StringUtils.isEmpty(flowControlConfig.getValue()) || StringUtils.isEmpty(flowControlConfig.getConfig()) || StringUtils.isEmpty(flowControlConfig.getType())) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        String key = buffer.append(serviceId).append(".").append(flowControlConfig.getValue()).toString();
        GuavaCountLimiter countLimiter = resourceRateLimiterMap.get(key);
        if (countLimiter == null) {
            //获得当前路径对应的流控次数
            Map<String, Integer> configMap = JSON.parseObject(flowControlConfig.getConfig(), Map.class);
            //判断是否包含流控规则
            if (!configMap.containsKey(FLOW_CTL_LIMIT_DURATION) || !configMap.containsKey(FLOW_CTL_LIMIT_PERMITS)) {
                return null;
            }
            //得到流控时间和时间内限制次数
            double permits = configMap.get(FLOW_CTL_LIMIT_PERMITS);
            countLimiter = new GuavaCountLimiter(permits);
            resourceRateLimiterMap.putIfAbsent(key, countLimiter);
        }
        return countLimiter;
    }

    /**
     * 获取令牌
     * @param permits 需要获取的令牌数量
     * @return 是否获取成功
     */
    public boolean acquire(int permits) {
        boolean success = rateLimiter.tryAcquire(permits);
        if (success) {
            return true;
        }
        return false;
    }
}
