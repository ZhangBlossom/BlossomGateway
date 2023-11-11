package blossom.project.core.netty.processor;


import blossom.project.core.context.HttpRequestWrapper;
/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 12:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 */
public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);

    void  start();

    void shutDown();
}
