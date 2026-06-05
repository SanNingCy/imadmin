package com.seekweb4.chat.modules.sys.utils.constant;

/**
 * @author coderpwh
 */
public final class RedisKeyUtilConstant {

    private RedisKeyUtilConstant() {
        throw new IllegalStateException("常量类不允许实例化");
    }




    private static final String MODULE_GOOGLE = "google:admin";




    // 谷歌相关 Key 模板
    public static final String GOOGLE_INFOMON_KEY = MODULE_GOOGLE + ":info:mon:user:";




}
