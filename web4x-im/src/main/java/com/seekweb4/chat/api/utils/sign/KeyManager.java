package com.seekweb4.chat.api.utils.sign;

//import com.seekweb4.chat.asset.config.AssetConfig;
import com.seekweb4.chat.api.config.AssetConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:43
 */
@Slf4j
@Component
public class KeyManager {


    @Resource
    private AssetConfig assetConfig;


    /***
     * 获取公钥
     * @param appId
     * @return
     */

    public String getPublicKey(String appId) {
        log.info("获取rsa公钥,appId:{}", appId);
        if (appId.equals(assetConfig.getAppId())) {
            log.info("获取rsa公钥成功");
            return assetConfig.getPublicKey();
        } else {
            return "";
        }
    }
}
