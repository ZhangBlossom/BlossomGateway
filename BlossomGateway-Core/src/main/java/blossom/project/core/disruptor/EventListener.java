package blossom.project.core.disruptor;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/13 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 事件监听器
 * 监听接口
 */
public interface EventListener<E> {

    void onEvent(E event);

    /**
     *
     * @param ex
     * @param sequence 异常执行顺序
     * @param event
     */
    void onException(Throwable ex,long sequence,E event);

}
