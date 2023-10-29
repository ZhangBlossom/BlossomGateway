package blossom.project.config.center.api;





/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 19:24
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 配置中心接口方法
 */


public interface ConfigCenter {

    /**
     * 初始化配置中心配置
     * @param serverAddr  配置中心地址
     * @param env 环境
     */
    void init(String serverAddr, String env);


    /**
     * 订阅配置中心配置变更
     * @param listener  配置变更监听器
     */
    void subscribeRulesChange(RulesChangeListener listener);
}
