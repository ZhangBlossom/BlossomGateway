package blossom.project.rpc.core.entity;

import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/17 01:02
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcRequestCache类
 * 1: 当前类得用来生成请求ID
 * 1.1：可以用雪花/时间戳/AtomcInteger 各有各的问题
 * 2：当前类得存储Promise的信息
 */
@Data
public class RpcCache<T> {

    //TODO 使用时间戳未必不重复
    //还可以考虑用雪花/自研ID生成器/AtomicInteger
    public static long getRequestId() {
        return ATOMICLONG_REQ_ID_GENERATOR.getAndIncrement();
        //return System.currentTimeMillis();
    }

    private static final AtomicLong ATOMICLONG_REQ_ID_GENERATOR = new AtomicLong(1);

    /**
     * RpcResponse缓存
     * 用来由于Client端接收到的响应数据
     * 暂时先缓存一下异步的响应数据，等真拿到了再处理
     *
     */

    public static final  Map<Long, DefaultPromise<RpcResponse>> RESPONSE_CACHE
            = new ConcurrentHashMap<>();

}
