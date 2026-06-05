package com.seekweb4.chat.config.logConfig;

import cn.hutool.core.lang.UUID;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @author tt
 */
public class ThreadMdcUtils {
    public static Runnable wrapAsync(Runnable task, Map<String,String> context){
        return () -> {
            if(context==null){
                MDC.clear();
            }else {
                MDC.setContextMap(context);
            }
            if(MDC.get("traceId")==null){
                MDC.put("traceId", UUID.fastUUID().toString(true));
            }
            try {
                task.run();
            }finally {
                MDC.clear();
            }
        };
    }
}
