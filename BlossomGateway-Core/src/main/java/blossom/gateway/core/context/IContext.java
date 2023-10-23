package blossom.gateway.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * @author: 张锦标
 * @date: 2023/10/23 14:12
 * Context接口
 * 顶层接口
 * 网关请求上下文生命周期接口
 */
public interface IContext {

    /**
     * 上下文生命周期，运行中状态
     */
    int RUNNING = 1;

    /**
     * 运行过程中出错，标记字段，告诉我们请求以及结束，需要写回客户端
     */
    int WRITTEN = 0;

    /**
     * 标记写回成功,防止并发情况下的多次写回
     */

    int COMPLETED = 2;

    /**
     * 表示网关请求结束
     */
    int TERMINATED = 3;

    /**
     * 设置上下文状态为运行中
     */
    void run();

    /**
     * 设定上下文状态为标记写回
     */
    void written();

    /**
     * 写回结束也需要进行上下文状态写回
     */
    void completed();

    /**
     * 设定上下文状态为请求结束
     */
    void terminated();

    /**
     * 判断网关状态
     */
    boolean isRunning();

    boolean isWritten();

    boolean isCompleted();

    boolean isTerminated();

    /**
     * 获取协议
     */
    String getProtocal();

    /**
     * 获取请求对象
     */
    Object getRequest();

    /**
     * 获取返回对象
     */
    Object getResponse();

    /**
     * 获取异常对象
     */
    Throwable getThrowable();

    /**
     * 设置返回对象
     */
    void setResponse(Object response);

    /**
     * 设置异常信息
     */
    void setThrowable(Throwable throwable);

    /**
     * 获取netty上下文
     */
    ChannelHandlerContext getNettyContext();

    /**
     * 是否是长连接
     */
    boolean keepAlive();

    /**
     * 释放请求资源
     */
    void releaseRequest();

    /**
     * 设置写回接收回调函数
     */
    void setCompletedCallback(Consumer<IContext> consumer);

    /**
     * 执行写回接收回调函数
     */
    void invokeCompletedCallback();

}
