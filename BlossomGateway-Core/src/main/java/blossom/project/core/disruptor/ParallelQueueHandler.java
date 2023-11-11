package blossom.project.core.disruptor;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @param <E> 队列中存储的事件类型
 * @author: ZhangBlossom
 * @date: 2023/11/13 18:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 基于Disruptor实现的多生产者多消费组无锁队列
 * 这个类的主要作用是基于 Disruptor 实现的多生产者多消费者无锁队列，
 * 通过 Builder 模式进行灵活的参数配置。其中使用了 Disruptor 的一些核心概念，如
 * RingBuffer、WaitStrategy、WorkerPool 等，以实现高性能的事件处理。
 */
public class ParallelQueueHandler<E> implements ParallelQueue<E> {

    /**
     * 环形缓冲区 内部缓冲区存放我们的事件Holder类
     */
    private RingBuffer<Holder> ringBuffer;

    /**
     * 事件监听器
     */
    private EventListener<E> eventListener;

    /**
     * 工作线程池
     */
    private WorkerPool<Holder> workerPool;

    /**
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * Disruptor 框架中的一个接口，用于在事件发布（publish）时将数据填充到事件对象中
     */
    private EventTranslatorOneArg<Holder, E> eventTranslator;

    /**
     * 构造方法，通过 Builder 模式初始化 Disruptor 队列
     *
     * @param builder Disruptor 队列的构建器
     */
    public ParallelQueueHandler(Builder<E> builder) {
        this.executorService = Executors.newFixedThreadPool(builder.threads,
                new ThreadFactoryBuilder().setNameFormat("ParallelQueueHandler" + builder.namePrefix + "-pool-%d").build());

        this.eventListener = builder.listener;
        this.eventTranslator = new HolderEventTranslator();

        // 创建 RingBuffer
        RingBuffer<Holder> ringBuffer = RingBuffer.create(builder.producerType, new HolderEventFactory(),
                builder.bufferSize, builder.waitStrategy);

        // 通过 RingBuffer 创建屏障 (固定流程）
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        // 创建多个消费者组
        WorkHandler<Holder>[] workHandlers = new WorkHandler[builder.threads];
        for (int i = 0; i < workHandlers.length; i++) {
            workHandlers[i] = new HolderWorkHandler();
        }

        // 创建多消费者线程池
        WorkerPool<Holder> workerPool = new WorkerPool<>(ringBuffer, sequenceBarrier, new HolderExceptionHandler(),
                workHandlers);
        // 设置多消费者的 Sequence 序号，主要用于统计消费进度，
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        this.workerPool = workerPool;
    }

    /**
     * 将事件添加到队列
     *
     * @param event 要添加的事件
     */
    @Override
    public void add(E event) {
        final RingBuffer<Holder> holderRing = ringBuffer;
        if (holderRing == null) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), event);
        }
        try {
            ringBuffer.publishEvent(this.eventTranslator, event);
        } catch (NullPointerException e) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), event);
        }
    }

    /**
     * 将多个事件添加到队列
     *
     * @param events 要添加的事件数组
     */
    @Override
    public void add(E... events) {
        final RingBuffer<Holder> holderRing = ringBuffer;
        if (holderRing == null) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), events);
        }
        try {
            ringBuffer.publishEvents(this.eventTranslator, events);
        } catch (NullPointerException e) {
            process(this.eventListener, new IllegalStateException("ParallelQueueHandler is close"), events);
        }
    }

    /**
     * 尝试将事件添加到队列
     *
     * @param event 要添加的事件
     * @return 是否成功添加
     */
    @Override
    public boolean tryAdd(E event) {
        final RingBuffer<Holder> holderRing = ringBuffer;
        if (holderRing == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvent(this.eventTranslator, event);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * 尝试将多个事件添加到队列
     *
     * @param events 要添加的事件数组
     * @return 是否成功添加
     */
    @Override
    public boolean tryAdd(E... events) {
        final RingBuffer<Holder> holderRing = ringBuffer;
        if (holderRing == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvents(this.eventTranslator, events);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * 启动队列
     */
    @Override
    public void start() {
        this.ringBuffer = workerPool.start(executorService);
    }

    /**
     * 关闭队列
     */
    @Override
    public void shutDown() {
        RingBuffer<Holder> holder = ringBuffer;
        ringBuffer = null;
        if (holder == null) {
            return;
        }
        if (workerPool != null) {
            workerPool.drainAndHalt();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 判断队列是否关闭
     *
     * @return 队列是否关闭
     */
    @Override
    public boolean isShutDown() {
        return ringBuffer == null;
    }

    /**
     * 处理异常的静态方法，用于调用事件监听器的异常处理方法
     *
     * @param listener 事件监听器
     * @param e        异常
     * @param event    事件
     * @param <E>      事件类型
     */
    private static <E> void process(EventListener<E> listener, Throwable e, E event) {
        listener.onException(e, -1, event);
    }

    /**
     * 处理异常的静态方法，用于调用事件监听器的异常处理方法
     *
     * @param listener 事件监听器
     * @param e        异常
     * @param events   事件数组
     * @param <E>      事件类型
     */
    private static <E> void process(EventListener<E> listener, Throwable e, E... events) {
        for (E event : events) {
            process(listener, e, event);
        }
    }

    /**
     * Builder 建造者模式
     *
     * @param <E> 队列中存储的事件类型
     */
    public static class Builder<E> {
        /**
         * 生产者类型 默认使用多生产者类型
         */
        private ProducerType producerType = ProducerType.MULTI;
        /**
         * 线程队列大小
         */
        private int bufferSize = 1024 * 16;
        /**
         * 工作线程默认为1
         */
        private int threads = 1;
        /**
         * 前缀 定位模块
         */
        private String namePrefix = "";
        /**
         * 等待策略
         */
        private WaitStrategy waitStrategy = new BlockingWaitStrategy();
        /**
         * 监听器
         */
        private EventListener<E> listener;

        // 设置生产者类型，默认为多生产者类型
        public Builder<E> setProducerType(ProducerType producerType) {
            Preconditions.checkNotNull(producerType);
            this.producerType = producerType;
            return this;
        }

        // 设置线程队列大小，要求是2的幂次方
        public Builder<E> setBufferSize(int bufferSize) {
            Preconditions.checkArgument(Integer.bitCount(bufferSize) == 1);
            this.bufferSize = bufferSize;
            return this;
        }

        // 设置工作线程数
        public Builder<E> setThreads(int threads) {
            Preconditions.checkArgument(threads > 0);
            this.threads = threads;
            return this;
        }

        // 设置线程名前缀
        public Builder<E> setNamePrefix(String namePrefix) {
            Preconditions.checkNotNull(namePrefix);
            this.namePrefix = namePrefix;
            return this;
        }

        // 设置等待策略，默认为 BlockingWaitStrategy
        public Builder<E> setWaitStrategy(WaitStrategy waitStrategy) {
            Preconditions.checkNotNull(waitStrategy);
            this.waitStrategy = waitStrategy;
            return this;
        }

        // 设置事件监听器
        public Builder<E> setListener(EventListener<E> listener) {
            Preconditions.checkNotNull(listener);
            this.listener = listener;
            return this;
        }

        // 构建 ParallelQueueHandler 对象
        public ParallelQueueHandler<E> build() {
            return new ParallelQueueHandler<>(this);
        }
    }

    /**
     * 事件对象
     */
    public class Holder {
        /**
         * 事件
         */
        private E event;

        // 设置事件的值
        public void setValue(E event) {
            this.event = event;
        }

        // 重写 toString 方法，用于调试时打印事件信息
        @Override
        public String toString() {
            return "Holder{" + "event=" + event + '}';
        }
    }

    // 异常处理器
    private class HolderExceptionHandler implements ExceptionHandler<Holder> {

        @Override
        public void handleEventException(Throwable throwable, long l, Holder event) {
            Holder holder = (Holder) event;
            try {
                eventListener.onException(throwable, l, holder.event);
            } catch (Exception e) {
                // 异常处理时出现异常的话，可以在这里进行额外的处理
            } finally {
                holder.setValue(null);
            }
        }

        @Override
        public void handleOnStartException(Throwable throwable) {
            throw new UnsupportedOperationException(throwable);
        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {
            throw new UnsupportedOperationException(throwable);
        }
    }

    // 消费者工作处理器
    private class HolderWorkHandler implements WorkHandler<Holder> {
        @Override
        public void onEvent(Holder holder) throws Exception {
            // 调用事件监听器的处理事件方法
            eventListener.onEvent(holder.event);
            // 处理完事件后，将事件置为空，帮助 GC 回收资源
            holder.setValue(null);
        }
    }

    // 事件工厂，用于创建事件对象
    private class HolderEventFactory implements EventFactory<Holder> {

        @Override
        public Holder newInstance() {
            return new Holder();
        }
    }

    // 事件翻译器，用于将事件数据填充到事件对象中
    private class HolderEventTranslator implements EventTranslatorOneArg<Holder, E> {
        @Override
        public void translateTo(Holder holder, long l, E e) {
            // 将事件数据填充到 Holder 对象中
            holder.setValue(e);
        }
    }
}

