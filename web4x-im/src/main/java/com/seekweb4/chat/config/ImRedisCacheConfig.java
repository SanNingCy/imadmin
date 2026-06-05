package com.seekweb4.chat.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * IM Spring Cache（Redis），与若依 {@code com.web4x.common.core.redis.RedisConfig} 区分。
 */
@Configuration
public class ImRedisCacheConfig {

    @Bean("imSpringCacheManager")
    @Primary
    public CacheManager imSpringCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        ClassLoader loader = this.getClass().getClassLoader();
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
        RedisSerializationContext.SerializationPair<Object> pair =
                RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer);
        RedisCacheConfiguration defaultCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        return RedisCacheManager.builder(redisCacheWriter)
                .withCacheConfiguration("IMToken", defaultCacheConfig.entryTtl(Duration.ofDays(30)))
                .withCacheConfiguration("IMUser", defaultCacheConfig.entryTtl(Duration.ofDays(30)))
                .withCacheConfiguration("chatRoomAPIAppToken", defaultCacheConfig.entryTtl(Duration.ofDays(30)))
                .withCacheConfiguration("chatRoomAPIUserToken", defaultCacheConfig.entryTtl(Duration.ofDays(30)))
                .cacheDefaults(defaultCacheConfig)
                .build();
    }
}
