package blossom.project.core.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @author: ZhangBlossom
 * @date: 2022/11/19 15:18
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JedisUtil
 * Redis-Jedis工具包
 */
@Slf4j
public class JedisUtil {
    public static ReentrantLock lock = new ReentrantLock();
    private final String DIST_LOCK_SUCCESS = "OK";
    private final Long DIST_LOCK_RELEASE_SUCCESS = 1L;
    private final String SET_IF_NOT_EXIST = "NX";
    private final String SET_WITH_EXPIRE_TIME = "PX";
    private JedisPoolUtil jedisPool = new JedisPoolUtil();

    public boolean setString(String key, String value) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            log.debug("setString() key {} throws:{}", key, e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public boolean setStringEx(String key, int seconds, String value) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.setex(key, seconds, value);
            return true;
        } catch (Exception e) {
            log.debug("setStringEx() key {} throws:{}",key, e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public String getString(String key) {
        Jedis jedis = jedisPool.getJedis();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            log.debug("getString() key {} throws:{}", key,e.getMessage());
            return null;
        } finally {
            close(jedis);
        }
    }

    public boolean delString(String key) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.del(key);
            return true;
        } catch (Exception e) {
            log.debug("delString() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public boolean delHash(String key, String mKey) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.hdel(key, mKey);
            return true;
        } catch (Exception e) {
            log.debug("setHash() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public boolean setHash(String key, String mKey, String mVal) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.hset(key, mKey, mVal);
            return true;
        } catch (Exception e) {
            log.debug("setHash() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public String getHash(String key, String mKey) {
        Jedis jedis = jedisPool.getJedis();
        try {
            return jedis.hget(key, mKey);
        } catch (Exception e) {
            log.debug("setHash() key {} throws:{}", key,e.getMessage());
        } finally {
            close(jedis);
        }
        return null;
    }

    public boolean setHashMulti(String key, Map<String, String> map) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.hmset(key, map);
            return true;
        } catch (Exception e) {
            log.debug("setMHash() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public List<String> getHashMulti(String key, String[] members) {
        Jedis jedis = jedisPool.getJedis();
        try {
            return jedis.hmget(key, members);
        } catch (Exception e) {
            log.debug("getHashMulti() key {} throws:{}", key,e.getMessage());
        } finally {
            close(jedis);
        }
        return null;
    }

    public List<String> getHashValsAll(String key) {
        Jedis jedis = jedisPool.getJedis();
        try {
            return jedis.hvals(key);
        } catch (Exception e) {
            log.debug("getHashValsAll() key {} throws:{}", key,e.getMessage());
        } finally {
            close(jedis);
        }
        return null;
    }

    public Set<String> getHashKeysAll(String key) {
        Jedis jedis = jedisPool.getJedis();
        try {
            return jedis.hkeys(key);
        } catch (Exception e) {
            log.debug("getHashValsAll() key {} throws:{}", key,e.getMessage());
        } finally {
            close(jedis);
        }
        return null;
    }

    public boolean addScoreSet(String key, String mKey, int score) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.zadd(key, score, mKey);
            return true;
        } catch (Exception e) {
            log.debug("addScoreSet() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public boolean delScoreSet(String key, String mKey) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.zrem(key, mKey);
            return true;
        } catch (Exception e) {
            log.debug("delScoreSet() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public boolean changeScoreSet(String key, String mKey, int score) {
        Jedis jedis = jedisPool.getJedis();
        try {
            jedis.zincrby(key, score, mKey);
            return true;
        } catch (Exception e) {
            log.debug("changeScoreSet() key {} throws:{}", key,e.getMessage());
            return false;
        } finally {
            close(jedis);
        }
    }

    public Set<String> listScoreSetString(String key, int start, int end, boolean asc) {
        Jedis jedis = jedisPool.getJedis();
        try {
            if (asc) {
                return jedis.zrange(key, start, end);
            } else {
                return jedis.zrevrange(key, start, end);
            }
        } catch (Exception e) {
            log.debug("listScoreSetString() key {} throws:{}", key,e.getMessage());
        } finally {
            close(jedis);
        }
        return null;
    }

    public Set<Tuple> listScoreSetTuple(String key, int start, int end, boolean asc) {
        Jedis jedis = jedisPool.getJedis();
        try {
            if (asc) {
                return jedis.zrangeWithScores(key, start, end);
            } else {
                return jedis.zrevrangeWithScores(key, start, end);
            }
        } catch (Exception e) {
            log.debug("listScoreSetString() key {} throws:{}", key,e.getMessage());
        } finally {
            close(jedis);
        }
        return null;
    }

    public boolean getDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = jedisPool.getJedis();
        try {
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (DIST_LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.debug("getDistributedLock throws {}", e);
        } finally {
            close(jedis);
        }
        return false;
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedis = jedisPool.getJedis();
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (DIST_LOCK_RELEASE_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.debug("releaseDistributedLock throws {}", e.getMessage());
        } finally {
            close(jedis);
        }
        return false;

    }

    public void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public Object executeScript(String key, int limit, int expire){
        Jedis jedis = jedisPool.getJedis();
        String lua = buildLuaScript();
        //加载Lua脚本到Redis中 以获得脚本的SHA-1散列值
        String scriptLoad =jedis.scriptLoad(lua);
        try {
            //尝试执行Lua脚本
            Object result = jedis.evalsha(scriptLoad, Arrays.asList(key), Arrays.asList(String.valueOf(expire), String.valueOf(limit)));
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    // 构造lua脚本
    private static String buildLuaScript() {
        String lua = "local num = redis.call('incr', KEYS[1])\n" +
                "if tonumber(num) == 1 then\n" +
                "\tredis.call('expire', KEYS[1], ARGV[1])\n" +
                "\treturn 1\n" +
                "elseif tonumber(num) > tonumber(ARGV[2]) then\n" +
                "\treturn 0\n" +
                "else \n" +
                "\treturn 1\n" +
                "end\n";
        return lua;
    }



}
