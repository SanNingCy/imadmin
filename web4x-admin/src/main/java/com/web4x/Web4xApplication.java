package com.web4x;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.quartz.autoconfigure.QuartzAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import com.web4x.framework.config.Web4xMonitorSessionConfig;

/**
 * 启动程序
 * 
 * @author web4x
 */
@SpringBootApplication(
        exclude = { DataSourceAutoConfiguration.class, QuartzAutoConfiguration.class },
        scanBasePackages = { "com.web4x", "com.seekweb4.chat" })
@Import(Web4xMonitorSessionConfig.class)
public class Web4xApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(Web4xApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  web4x启动成功   ლ(´ڡ`ლ)ﾞ ");
    }
}