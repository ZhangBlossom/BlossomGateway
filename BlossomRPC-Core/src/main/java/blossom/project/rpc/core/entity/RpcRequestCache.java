package blossom.project.rpc.core.entity;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 23:44
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcRequestCache类
 * 1:
 */
@Data
public class RpcRequestCache {

    //TODO 使用时间戳未必不重复
    //还可以考虑用雪花/自研ID生成器/AtomicInteger
    public static long getRequestId() {
        return ATOMICLONG_REQ_ID_GENERATOR.getAndIncrement();
        //return System.currentTimeMillis();
    }

    private static final AtomicLong ATOMICLONG_REQ_ID_GENERATOR = new AtomicLong(1);


    public static final Map<Long, RpcPromise> REQUEST_CACHE_MAP = new ConcurrentHashMap<>();

}
