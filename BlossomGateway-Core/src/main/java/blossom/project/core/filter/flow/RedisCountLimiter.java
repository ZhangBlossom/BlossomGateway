package blossom.project.core.filter.flow;

import blossom.project.core.util.JedisUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/5 16:38
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RedisCountLimiter类提供Redis限流能力
 * Redis 用于实现分布式限流
 */
@Slf4j
public class RedisCountLimiter {

    protected JedisUtil jedisUtil;

    public RedisCountLimiter(JedisUtil jedisUtil) {
        this.jedisUtil = jedisUtil;
    }

    private static final int SUCCESS_RESULT = 1;
    private static final int FAILED_RESULT = 0;

    /**
     * 执行限流
     *
     * @param key    限流key 服务+路径
     * @param limit  限流次数
     * @param expire 超时时间
     * @return
     */
    public boolean doFlowControl(String key, int limit, int expire) {
        try {
            //执行脚本判断是否触发限流
            Object object = jedisUtil.executeScript(key, limit, expire);
            if (object == null) {
                return true;
            }
            Long result = Long.valueOf(object.toString());
            if (FAILED_RESULT == result) {
                return false;
            }
        } catch (Exception e) {
            log.error("分布式限流发送错误",e.getMessage());
            throw e;
        }
        return true;
    }


}
