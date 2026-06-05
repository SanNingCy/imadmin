package com.seekweb4.chat.modules.sys.utils;

import com.seekweb4.chat.common.utils.SpringContextHolder;
import com.seekweb4.chat.modules.sys.service.SysConfigService;

public class ConfigUtils {

    public static String getProductName(){
        return SpringContextHolder.getBean(SysConfigService.class).get("1").getProductName();
    }

    public static String getLogo(){
        return SpringContextHolder.getBean(SysConfigService.class).get("1").getLogo();
    }

    public static String getDefaultTheme(){
        return SpringContextHolder.getBean(SysConfigService.class).get("1").getDefaultTheme();
    }

    public static String getHomeUrl(){
        return SpringContextHolder.getBean(SysConfigService.class).get("1").getHomeUrl();
    }
}
