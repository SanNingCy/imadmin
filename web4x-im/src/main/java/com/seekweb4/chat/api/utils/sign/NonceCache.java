package com.seekweb4.chat.api.utils.sign;

import com.seekweb4.chat.common.utils.StringRedisUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 10:42
 */
@Component
public class NonceCache {


    @Resource
    private StringRedisUtils stringRedisUtils;

    public boolean checkAndAdd(String nonce, long expireSeconds) {
        String cacheKey = "NONCE:" + nonce;
        boolean exists=  stringRedisUtils.hasKey(cacheKey);
        if (exists) {
            return false;
        }
        stringRedisUtils.setEx(cacheKey, "1", expireSeconds, TimeUnit.SECONDS);
        return true;
    }
}
