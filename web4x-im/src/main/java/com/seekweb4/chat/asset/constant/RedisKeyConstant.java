package com.seekweb4.chat.asset.constant;

import java.io.Serializable;

/**
 * @author coderpwh
 */
public final class RedisKeyConstant {

    private RedisKeyConstant() {
        throw new IllegalStateException("常量类不允许实例化");
    }


    // Key 过期时间常量（单位：秒）
    public static final long EXPIRE_ONE_MINUTE = 60L;
    public static final long EXPIRE_FIVE_MINUTES = 300L;
    public static final long EXPIRE_ONE_HOUR = 3600L;
    public static final long EXPIRE_ONE_DAY = 86400L;
    public static final long EXPIRE_ONE_WEEK = 604800L;

    // 业务模块前缀
    private static final String MODULE_USER = "user";
    private static final String MODULE_ORDER = "order";

    private static final String MODULE_GOOGLE = "google";


    // 用户相关 Key 模板
    public static final String USER_INFO_KEY = MODULE_USER + ":info:%s";


    // 谷歌相关 Key 模板
    public static final String GOOGLE_INFOMON_KEY = MODULE_GOOGLE + ":info:mon:user:";


    // 资产相关
    public static final String TRANSACTION_LOCK_PREFIX = "transaction:lock:";
    public static final String IDEMPOTENT_KEY_PREFIX = "transaction:idempotent:";

    public static final String WITHDRAW_LOCK_PREFIX = "withdraw:lock:";
    public static final String WITHDRAW_IDEMPOTENT_PREFIX = "withdraw:idempotent:";


}
