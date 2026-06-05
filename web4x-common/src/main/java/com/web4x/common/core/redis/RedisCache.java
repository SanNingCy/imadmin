package com.web4x.common.core.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Shiro Cache的Redis实现。
 *
 * @author web4x
 */
public class RedisCache<K, V> implements Cache<K, V>
{
    private final String name;

    private final RedisTemplate<String, Object> redisTemplate;

    private final long timeout;

    public RedisCache(String name, RedisTemplate<String, Object> redisTemplate, long timeout)
    {
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.timeout = timeout;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) throws CacheException
    {
        return (V) redisTemplate.opsForValue().get(getRedisKey(key));
    }

    @Override
    public V put(K key, V value) throws CacheException
    {
        String redisKey = getRedisKey(key);
        if (timeout > 0)
        {
            redisTemplate.opsForValue().set(redisKey, value, timeout, TimeUnit.SECONDS);
        }
        else
        {
            redisTemplate.opsForValue().set(redisKey, value);
        }
        return value;
    }

    @Override
    public V remove(K key) throws CacheException
    {
        V previous = get(key);
        redisTemplate.delete(getRedisKey(key));
        return previous;
    }

    @Override
    public void clear() throws CacheException
    {
        redisTemplate.delete(redisTemplate.keys(getPrefix() + "*"));
    }

    @Override
    public int size()
    {
        return keys().size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys()
    {
        Set<String> keys = redisTemplate.keys(getPrefix() + "*");
        if (keys == null)
        {
            return Collections.emptySet();
        }
        return keys.stream().map(key -> (K) key.substring(getPrefix().length())).collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values()
    {
        Set<K> keys = keys();
        if (keys.isEmpty())
        {
            return Collections.emptyList();
        }
        return keys.stream().map(this::get).collect(Collectors.toList());
    }

    private String getRedisKey(Object key)
    {
        return getPrefix() + String.valueOf(key);
    }

    private String getPrefix()
    {
        return "web4x:cache:" + name + ":";
    }
}
