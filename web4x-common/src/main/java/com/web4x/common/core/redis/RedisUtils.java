package com.web4x.common.core.redis;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import com.web4x.common.utils.spring.SpringUtils;

/**
 * Redis工具类
 *
 * @author web4x
 */
public class RedisUtils
{
    private RedisUtils()
    {
    }

    @SuppressWarnings("unchecked")
    private static RedisTemplate<String, Object> redisTemplate()
    {
        return SpringUtils.getBean("redisTemplate");
    }

    public static Object get(String key)
    {
        return redisTemplate().opsForValue().get(key);
    }

    public static void set(String key, Object value)
    {
        redisTemplate().opsForValue().set(key, value);
    }

    public static void set(String key, Object value, long timeout, TimeUnit unit)
    {
        redisTemplate().opsForValue().set(key, value, timeout, unit);
    }

    public static void delete(String key)
    {
        redisTemplate().delete(key);
    }

    public static void delete(Set<String> keys)
    {
        if (keys != null && !keys.isEmpty())
        {
            redisTemplate().delete(keys);
        }
    }

    public static Set<String> keys(String pattern)
    {
        return redisTemplate().keys(pattern);
    }
}
