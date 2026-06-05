package com.seekweb4.chat.config;

import com.seekweb4.chat.agora.utils.RedisUtil;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.Duration;

/**
 * IM 业务模块启动配置（由原 WebApplication 拆分，避免与 Web4xApplication 冲突）
 */
@Configuration
@EnableScheduling
@EnableCaching
@EnableAsync
@ServletComponentScan("com.seekweb4.chat")
@EnableMongoRepositories(basePackages = "com.seekweb4.chat")
public class ImBootstrapConfig {

    private static final String LOCK_REGISTRY_KEY = "lock";
    private static final Duration LOCK_RELEASE_TIME_DURATION = Duration.ofSeconds(30);

    @Bean("imTransactionManager")
    public PlatformTransactionManager imTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(
            @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisCacheTemplate) {
        return redisCacheTemplate.opsForHash();
    }

    @Bean
    public ListOperations<String, Object> listOperations(
            @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisCacheTemplate) {
        return redisCacheTemplate.opsForList();
    }

    @Bean
    public RedisTemplate<String, Object> redisCacheTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, LOCK_REGISTRY_KEY, LOCK_RELEASE_TIME_DURATION.toMillis());
    }

    @Bean
    public RedisUtil redisUtil(StringRedisTemplate stringRedisTemplate,
                               @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisCacheTemplate,
                               RedisLockRegistry redisLockRegistry) {
        return new RedisUtil(stringRedisTemplate, redisCacheTemplate, redisLockRegistry);
    }
}
