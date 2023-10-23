package blossom.gateway.core.rule;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 15:11
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Rule类
 * 规则对象，需要进行规则匹配比较
 */
@Data
public class Rule implements Comparable<Rule>, Serializable {


    /**
     * 全局唯一规则ID
     */
    private String id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 规则协议
     */
    private String protocol;

    /**
     * 规则优先级
     */
    private Integer order;

    /**
     * 过滤器配置对象
     */
    private Set<FilterConfig> filterConfigs = new HashSet<>();


    public Rule() {
        super();
    }

    public Rule(String id, String name, String protocol, Integer order, Set<FilterConfig> filterConfigs) {
        super();
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.order = order;
        this.filterConfigs = filterConfigs;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FilterConfig {
        /**
         * 规则配置id
         */
        private String id;

        /**
         * 配置信息
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
            return Objects.hashCode(id);
        }
    }


    /**
     * 想规则中添加一些新配置的方法
     *
     * @param filterConfig
     * @return
     */
    public boolean addFilterConfig(FilterConfig filterConfig) {
        return this.filterConfigs.add(filterConfig);
    }


    /**
     * 判断对于的过滤器配置是否存在
     * @param id 要查询的过滤器配置id
     * @return
     */
    public boolean existFilterConfig(String id){
        return this.filterConfigs.stream()
                .anyMatch(filterConfig -> StringUtils.equalsIgnoreCase(id, filterConfig.id));
    }

    /**
     * 根据id获取过滤器配置中的某一个过滤器配置
     * @param id
     * @return
     */
    public FilterConfig getFilterConfig(String id) {
        return this.filterConfigs.stream()
                .filter(filterConfig -> StringUtils.equalsIgnoreCase(id, filterConfig.id))
                .findFirst()
                .get();
    }


    /**
     * @param rule the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Rule rule) {
        int compareRule =  Integer.compare(this.getOrder(),rule.getOrder());
        if (compareRule==0){
            return this.getId().compareTo(rule.getId());
        }
        return compareRule;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || getClass() != o.getClass()) {
            return false;
        }
        Rule that = (Rule) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
