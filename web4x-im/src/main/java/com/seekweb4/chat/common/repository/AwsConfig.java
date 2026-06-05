package com.seekweb4.chat.common.repository;



import com.seekweb4.chat.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.AccelerateConfiguration;
import software.amazon.awssdk.services.s3.model.BucketAccelerateStatus;
import software.amazon.awssdk.services.s3.model.PutBucketAccelerateConfigurationRequest;

import java.net.URI;
import java.time.Duration;

/**
 * AWS S3 配置类
 * 性能优化：在启动时一次性初始化 S3 客户端，供全局复用
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "config.accessory.type", havingValue = "aws")
public class AwsConfig {


    @Value("${config.accessory.aws.accessKey}")
    private String accessKey;

    @Value("${config.accessory.aws.secretKey}")
    private String secretKey;

    @Value("${config.accessory.aws.bucketName}")
    private String bucketName;

    @Value("${config.accessory.aws.endpoint}")  // 使用endpoint而不是accessUrl
    private String endpoint;

    @Value("${config.accessory.aws.region}")  // 添加region配置
    private String region;

    @Bean
    public S3Client getS3Client() {
        S3ClientBuilder s3Builder = S3Client.builder()
                // 使用配置的region
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(30))
                        .socketTimeout(Duration.ofMinutes(2))
                        .connectionMaxIdleTime(Duration.ofMinutes(5))
                );

        // 如果配置了自定义endpoint，就使用它
        if (StringUtils.isNotBlank(endpoint) && !endpoint.contains("amazonaws.com")) {
            try {
                s3Builder.endpointOverride(URI.create(endpoint.trim()));
            } catch (Exception e) {
                log.warn("无效的endpoint配置: {}, 使用默认AWS端点", endpoint);
            }
        }

        S3Client s3Client = s3Builder.build();

        // 只在AWS标准端点上启用加速
        if (endpoint == null || endpoint.contains("amazonaws.com")) {
            try {
                s3Client.putBucketAccelerateConfiguration(
                        PutBucketAccelerateConfigurationRequest.builder()
                                .bucket(bucketName)
                                .accelerateConfiguration(AccelerateConfiguration.builder()
                                        .status(BucketAccelerateStatus.ENABLED)
                                        .build())
                                .build());
                log.info("S3传输加速已启用");
            } catch (Exception e) {
                log.warn("无法配置S3加速: {}", e.getMessage());
            }
        }

        return s3Client;
    }

}