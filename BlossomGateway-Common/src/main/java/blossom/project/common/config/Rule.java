package blossom.project.common.config;

import java.io.Serializable;
import java.util.HashSet;
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
     * 规则排序，对应场景：一个路径对应多条规则，然后只执行一条规则的情况
     */
    private Integer order;

    private Set<FilterConfig> filterConfigs =new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Set<FilterConfig> getFilterConfigs() {
        return filterConfigs;
    }

    public void setFilterConfigs(Set<FilterConfig> filterConfigs) {
        this.filterConfigs = filterConfigs;
    }

    public Rule(){
        super();
    }

    public Rule(String id, String name, String protocol, Integer order, Set<FilterConfig> filterConfigs) {
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.order = order;
        this.filterConfigs = filterConfigs;
    }

    public static class FilterConfig{

        /**
         * 过滤器唯一ID
         */
        private String id;
        /**
         * 过滤器规则描述，{"timeOut":500,"balance":random}
         */
        private String config;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        @Override
        public  boolean equals(Object o){
            if (this == o) {
                return  true;
            }

            if((o== null) || getClass() != o.getClass()){
                return false;
            }

            FilterConfig that =(FilterConfig)o;
            return id.equals(that.id);
        }

        @Override
        public  int hashCode(){
            return Objects.hash(id);
        }
    }

    /**
     * 向规则里面添加过滤器
     * @param filterConfig
     * @return
     */
     public boolean addFilterConfig(FilterConfig filterConfig){
            return filterConfigs.add(filterConfig);
     }

    /**
     * 通过一个指定的FilterID获取FilterConfig
     * @param id
     * @return
     */
     public  FilterConfig getFilterConfig(String id){
         for(FilterConfig config:filterConfigs){
             if(config.getId().equalsIgnoreCase(id)){
                return  config;
             }

         }
         return null;
     }

    /**
     * 根据filterID判断当前Rule是否存在
     * @return
     */
    public boolean hashId(){
        for(FilterConfig config:filterConfigs){
            if(config.getId().equalsIgnoreCase(id)){
                return  true;
            }
        }
        return false;
    }


    @Override
    public int compareTo(Rule o) {
        int  orderCompare = Integer.compare(getOrder(),o.getOrder());
        if(orderCompare == 0){
          return getId().compareTo(o.getId());
        }
        return orderCompare;
    }

    @Override
    public  boolean equals(Object o){
        if (this == o) {
            return  true;
        }

        if((o== null) || getClass() != o.getClass()){
            return false;
        }

        FilterConfig that =(FilterConfig)o;
        return id.equals(that.id);
    }

    @Override
    public  int hashCode(){
        return Objects.hash(id);
    }
}
