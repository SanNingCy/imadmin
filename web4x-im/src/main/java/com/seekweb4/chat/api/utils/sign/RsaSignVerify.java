package com.seekweb4.chat.api.utils.sign;
import java.lang.annotation.*;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:41
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RsaSignVerify {
    /**
     * 是否校验时间戳
     */
    boolean checkTimestamp() default true;

    /**
     * 是否校验随机串 nonce
     */
    boolean checkNonce() default true;

    /**
     * 时间戳有效期（秒）// 默认5分钟内有效
     */
    long expireSeconds() default 300;

}