package blossom.gateway.core;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 22:46
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * LifeCycle接口
 */
public interface LifeCycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();


    /**
     * 关闭
     */
    void shutdown();


}
