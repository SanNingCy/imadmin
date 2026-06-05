package com.web4x.common.core.redis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Shiro CacheManager的Redis实现。
 *
 * @author web4x
 */
public class RedisCacheManager implements CacheManager
{
    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<String, Long> cacheTimeouts;

    public RedisCacheManager(RedisTemplate<String, Object> redisTemplate, Map<String, Long> cacheTimeouts)
    {
        this.redisTemplate = redisTemplate;
        this.cacheTimeouts = cacheTimeouts == null ? Collections.emptyMap() : new HashMap<>(cacheTimeouts);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException
    {
        return new RedisCache<>(name, redisTemplate, cacheTimeouts.getOrDefault(name, 0L));
    }
}
