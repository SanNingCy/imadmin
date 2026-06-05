package com.seekweb4.chat.delayedQueue.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis延时队列配置类
 */
@Configuration
public class RedisDelayedQueueConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.timeout:10s}")
    private java.time.Duration redisTimeout;

    /**
     * 配置Redisson客户端
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 单节点模式配置
        String address = "redis://" + redisHost + ":" + redisPort;
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisDatabase)
                .setTimeout((int) redisTimeout.toMillis())
                .setConnectionPoolSize(16)
                .setConnectionMinimumIdleSize(4)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(10000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        
        // 如果设置了密码
        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.useSingleServer().setPassword(redisPassword);
        }
        
        // 集群模式配置（如果需要）
        // config.useClusterServers()
        //         .addNodeAddress("redis://127.0.0.1:7004", "redis://127.0.0.1:7001", "redis://127.0.0.1:7000")
        //         .setPassword("password");
        
        return Redisson.create(config);
    }
}
