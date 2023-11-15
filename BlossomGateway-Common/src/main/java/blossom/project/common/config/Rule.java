package blossom.project.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Rule规则类
 */
@Data
@AllArgsConstructor
@Builder
public class Rule implements Comparable<Rule>, Serializable {

    /**
     * 规则ID，全局唯一
     */
    private String id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 后端服务ID
     */
    private String serviceId;
    /**
     * 请求前缀
     */
    private String prefix;
    /**
     * 路径集合
     */
    private List<String> paths;
    /**
     * 规则排序，对应场景：一个路径对应多条规则，然后只执行一条规则的情况
     */
    private Integer order;

    /**
     * 过滤器配置信息
     */
    private Set<FilterConfig> filterConfigs = new HashSet<>();

    /**
     * 限流规则
     */
    private Set<FlowControlConfig> flowControlConfigs = new HashSet<>();
    /**
     * 重试规则
     */
    private RetryConfig retryConfig = new RetryConfig();
    /**
     * 熔断规则
     */
    private Set<HystrixConfig> hystrixConfigs = new HashSet<>();

    public Rule() {
        super();
    }

    public Rule(String id, String name, String protocol, String serviceId, String prefix, List<String> paths,
                Integer order, Set<FilterConfig> filterConfigs) {
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.serviceId = serviceId;
        this.prefix = prefix;
        this.paths = paths;
        this.order = order;
        this.filterConfigs = filterConfigs;
    }

    @Data
    public static class FilterConfig {

        /**
         * 过滤器唯一ID
         */
        private String id;
        /**
         * 过滤器规则描述，{"timeOut":500,"balance":random}
         */
        private String config;


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if ((o == null) || getClass() != o.getClass()) {
                return false;
            }

            FilterConfig that = (FilterConfig) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Data
    public static class FlowControlConfig {
        /**
         * 限流类型-可能是path，也可能是IP或者服务
         */
        private String type;
        /**
         * 限流对象的值
         */
        private String value;
        /**
         * 限流模式-单机还有分布式
         */
        private String model;
        /**
         * 限流规则,是一个JSON
         */
        private String config;
    }
    @Data
    public static class RetryConfig {
        private int times;

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }
    }

    @Data
    public static class HystrixConfig {
        /**
         * 熔断降级陆军
         */
        private String path;
        /**
         * 超时时间
         */
        private int timeoutInMilliseconds;
        /**
         * 核心线程数量
         */
        private int threadCoreSize;
        /**
         * 熔断降级响应
         */
        private String fallbackResponse;
    }

    /**
     * 向规则里面添加过滤器
     *
     * @param filterConfig
     * @return
     */
    public boolean addFilterConfig(FilterConfig filterConfig) {
        return filterConfigs.add(filterConfig);
    }

    /**
     * 通过一个指定的FilterID获取FilterConfig
     *
     * @param id
     * @return
     */
    public FilterConfig getFilterConfig(String id) {
        for (FilterConfig config : filterConfigs) {
            if (config.getId().equalsIgnoreCase(id)) {
                return config;
            }

        }
        return null;
    }

    /**
     * 根据filterID判断当前Rule是否存在
     *
     * @return
     */
    public boolean hashId(String id) {
        for (FilterConfig filterConfig : filterConfigs) {
            if (filterConfig.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Rule o) {
        int orderCompare = Integer.compare(getOrder(), o.getOrder());
        if (orderCompare == 0) {
            return getId().compareTo(o.getId());
        }
        return orderCompare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || getClass() != o.getClass()) {
            return false;
        }

        FilterConfig that = (FilterConfig) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
