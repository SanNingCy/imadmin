package com.seekweb4.chat.config.shiro.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.annotation.Resource;

/**
 * Created by lixinapp on 2018/2/20.
 */
public class RedisCacheManager implements CacheManager {


    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Value("${im.redis.cache-expire-seconds:${spring.data.redis.expireTime:${spring.redis.expireTime:3600}}}")
    private long redisExpireTime;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new RedisCache<K, V>(redisExpireTime, redisTemplate);// 为了简化代码的编写，此处直接new一个Cache
    }

}