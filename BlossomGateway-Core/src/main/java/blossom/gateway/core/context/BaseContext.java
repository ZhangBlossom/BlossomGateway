package blossom.gateway.core.context;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 14:45
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * BaseContext类
 */
public class BaseContext implements IContext {
    /**
     * 转发协议
     */
    protected final String protocol;

    /**
     * 状态 多线程情况下使用volatile比较合理
     */
    protected volatile int status;

    /**
     * 上下文参数
     */
    protected final Map<String, Object> attributes = new HashMap<>();
    /**
     * netty上下文
     */
    protected final ChannelHandlerContext nettyContext;

    /**
     * 异常
     */
    protected Throwable throwable;

    /**
     * 是否保持长连接
     */
    protected final boolean keepAlive;

    /**
     * 回调函数列表
     */
    protected List<Consumer<IContext>> completedCallBacks;

    /**
     * 定义是否已经释放资源
     */
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);

    public BaseContext(String protocol, ChannelHandlerContext nettyContext, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyContext = nettyContext;
        this.keepAlive = keepAlive;
    }

    @Override
    public void run() {
        status = IContext.RUNNING;
    }

    @Override
    public void written() {
        status = WRITTEN;
    }

    @Override
    public void completed() {
        status = COMPLETED;
    }

    @Override
    public void terminated() {
        status = TERMINATED;
    }

    @Override
    public boolean isRunning() {
        return status == IContext.RUNNING;
    }

    @Override
    public boolean isWritten() {
        return status == IContext.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return status == IContext.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return status == IContext.TERMINATED;
    }

    @Override
    public String getProtocal() {
        return this.protocol;
    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public void setResponse(Object response) {

    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public ChannelHandlerContext getNettyContext() {
        return this.nettyContext;
    }

    @Override
    public boolean keepAlive() {
        return this.keepAlive;
    }

    @Override
    public void releaseRequest() {
    }

    @Override
    public void setCompletedCallback(Consumer<IContext> consumer) {
        if (CollectionUtil.isEmpty(completedCallBacks)) {
            this.completedCallBacks = new ArrayList<>();
        }
        this.completedCallBacks.add(consumer);
    }

    @Override
    public void invokeCompletedCallback() {
        //非空则执行回调函数
        if (CollectionUtil.isNotEmpty(completedCallBacks)) {
            completedCallBacks.forEach(callBack -> callBack.accept(this));
        }
    }

    Object getAttribute(String key){
        return this.attributes.get(key);
    }
}
