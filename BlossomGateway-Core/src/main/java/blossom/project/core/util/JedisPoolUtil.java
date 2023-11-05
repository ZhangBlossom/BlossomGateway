package blossom.project.core.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;

import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: ZhangBlossom
 * @date: 2022/11/15 13:18
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JedisPoolUtil  Jedis连接池工具包
 */
@Slf4j
public class JedisPoolUtil {
    public static JedisPool jedisPool = null;
    private String host;
    private int port;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private boolean blockWhenExhausted;
    private int maxWaitMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;

    public static Lock lock = new ReentrantLock();

    private void initialConfig() {
        try {
            Properties prop = new Properties();
            //加载文件获取数据 文件带后缀
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("gateway.properties"));

            host = prop.getProperty("redis.host");
            port = Integer.parseInt(prop.getProperty("redis.port"));

            maxTotal = Integer.parseInt(prop.getProperty("redis.maxTotal"));
            maxIdle = Integer.parseInt(prop.getProperty("redis.maxIdle"));
            minIdle = Integer.parseInt(prop.getProperty("redis.minIdle"));
            /*blockWhenExhausted = Boolean.parseBoolean(prop.getProperty("redis.blockWhenExhausted"));
            maxWaitMillis = Integer.parseInt(prop.getProperty("redis.maxWaitMillis"));
            testOnBorrow = Boolean.parseBoolean(prop.getProperty("redis.testOnBorrow"));
            testOnReturn = Boolean.parseBoolean(prop.getProperty("redis.testOnReturn"));*/
        } catch (Exception e) {
            log.debug("parse configure file error.");
        }
    }

    /**
     * initial redis pool
     */
    private void initialPool() {
        if (lock.tryLock()) {
            try {
                lock.lock();
                initialConfig();
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(maxTotal);
                config.setMaxIdle(maxIdle);
                config.setMaxWaitMillis(maxWaitMillis);
                config.setTestOnBorrow(testOnBorrow);
                jedisPool = new JedisPool(config, host, port);
            } catch (Exception e) {
                log.debug("init redis pool failed : {}", e.getMessage());
            } finally {
                lock.unlock();
            }
        } else {
            log.debug("some other is init pool, just wait 1 second.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public Jedis getJedis() {

        if (jedisPool == null) {
            initialPool();
        }
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            log.debug("getJedis() throws : {}" + e.getMessage());
        }
        return null;
    }

    public Pipeline getPipeline() {
        BinaryJedis binaryJedis = new BinaryJedis(host, port);
        return binaryJedis.pipelined();
    }
}
