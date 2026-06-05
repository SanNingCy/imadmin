package com.web4x.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.web4x.framework.shiro.session.OnlineSessionDAO;
import com.web4x.framework.shiro.session.OnlineSessionFactory;

/**
 * IM Shiro 启用时 {@link ShiroConfig} 不加载，若依「在线用户」监控仍需要会话相关 Bean。
 */
@Configuration
public class Web4xMonitorSessionConfig {

    @Bean
    @ConditionalOnMissingBean(OnlineSessionFactory.class)
    public OnlineSessionFactory onlineSessionFactory() {
        return new OnlineSessionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(OnlineSessionDAO.class)
    public OnlineSessionDAO onlineSessionDAO() {
        return new OnlineSessionDAO();
    }
}
