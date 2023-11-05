package blossom.project.common.constant;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 12:28
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 负载均衡常量类
 */
public interface FilterConst {

    String LOAD_BALANCE_FILTER_ID = "load_balance_filter";
    String LOAD_BALANCE_FILTER_NAME = "load_balance_filter";
    int LOAD_BALANCE_FILTER_ORDER = 100;

    String LOAD_BALANCE_KEY = "load_balance";
    String LOAD_BALANCE_STRATEGY_RANDOM = "Random";
    String LOAD_BALANCE_STRATEGY_ROUND_ROBIN = "RoundRobin";


    String ROUTER_FILTER_ID = "router_filter";
    String ROUTER_FILTER_NAME = "router_filter";
    int ROUTER_FILTER_ORDER = Integer.MAX_VALUE;


    String AUTH_FILTER_ID = "auth_filter";

    String AUTH_FILTER_NAME = "auth_filter";

    int AUTH_FILTER_ORDER = 1;

    String FLOW_CTL_FILTER_ID = "flow_ctl_filter";
    String FLOW_CTL_FILTER_NAME = "flow_ctl_filter";
    int FLOW_CTL_FILTER_ORDER = 50;

    String FLOW_CTL_TYPE_PATH = "path";
    String FLOW_CTL_TYPE_SERVICE = "service";

    String FLOW_CTL_LIMIT_DURATION = "duration"; //以秒为单位
    String FLOW_CTL_LIMIT_PERMITS = "permits"; //允许请求的次数

    String FLOW_CTL_MODEL_DISTRIBUTED = "distributed";
    String FLOW_CTL_MODEL_SINGLETON = "Singleton";

}
