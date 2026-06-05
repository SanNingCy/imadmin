package com.seekweb4.chat.config.shiro.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCache<K, V> implements Cache<K, V> {

    private long expireTime = 3600;// 缓存的超时时间，单位为s

    private RedisTemplate<String, Object> redisTemplate;// 通过构造方法注入该对象

    public RedisCache() {
        super();
    }

    public RedisCache(long expireTime, RedisTemplate<String, Object> redisTemplate) {
        super();
        this.expireTime = expireTime;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 将key转换为Redis可用的String类型key，避免Shiro的SimplePrincipalCollection等复杂对象无法序列化的问题
     */
    private String getRedisKey(Object key) {
        return String.valueOf(key);
    }

    /**
     * 通过key来获取对应的缓存对象
     * 通过源码我们可以发现，shiro需要的key的类型为Object，V的类型为AuthorizationInfo对象
     */
    @Override
    public V get(K key) throws CacheException {
        return (V) redisTemplate.opsForValue().get(getRedisKey(key));
    }

    /**
     * 将权限信息加入缓存中
     */
    @Override
    public V put(K key, V value) throws CacheException {
        redisTemplate.opsForValue().set(getRedisKey(key), value, this.expireTime, TimeUnit.SECONDS);
        return value;
    }

    /**
     * 将权限信息从缓存中删除
     */
    @Override
    public V remove(K key) throws CacheException {
        String redisKey = getRedisKey(key);
        V v = (V) redisTemplate.opsForValue().get(redisKey);
        redisTemplate.delete(redisKey);
        return v;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

}
