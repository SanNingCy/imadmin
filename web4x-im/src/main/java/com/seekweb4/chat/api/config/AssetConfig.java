package com.seekweb4.chat.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author coderpwh
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "asset")
public class AssetConfig {


    private String appId;

    private String privateKey;

    private String publicKey;

    private String wxPayUrl;

}
