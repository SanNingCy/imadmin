package com.seekweb4.chat.config;

import com.seekweb4.chat.core.persistence.interceptor.PaginationInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * 确保 IM 自定义分页拦截器已注册。
 * <p>
 * web4x 集成时若依 {@code MyBatisConfig} 与 MyBatis-Plus 共存，{@code mybatis-config.xml}
 * 中的插件有时不会生效，导致 list 接口 count=0 且返回全表数据。
 */
@Configuration
public class ImPaginationMybatisConfig {

    private static final Logger log = LoggerFactory.getLogger(ImPaginationMybatisConfig.class);

    @Bean
    public BeanPostProcessor imPaginationInterceptorConfigurer() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof SqlSessionFactory factory) {
                    ensurePaginationInterceptor(factory.getConfiguration(), "beanPostProcessor:" + beanName);
                }
                return bean;
            }
        };
    }

    @EventListener(ContextRefreshedEvent.class)
    public void verifyPaginationInterceptor(ContextRefreshedEvent event) {
        event.getApplicationContext().getBeansOfType(SqlSessionFactory.class)
                .forEach((name, factory) -> {
                    org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
                    log.info("SqlSessionFactory [{}] interceptor count: {}", name, configuration.getInterceptors().size());
                    ensurePaginationInterceptor(configuration, "contextRefreshed:" + name);
                    configuration.getInterceptors().forEach(interceptor ->
                            log.info("SqlSessionFactory [{}] interceptor: {}", name, interceptor.getClass().getName()));
                });
    }

    private static void ensurePaginationInterceptor(org.apache.ibatis.session.Configuration configuration, String source) {
        boolean hasPagination = configuration.getInterceptors().stream()
                .anyMatch(interceptor -> interceptor instanceof PaginationInterceptor);
        if (!hasPagination) {
            configuration.addInterceptor(new PaginationInterceptor());
            log.warn("Registered IM PaginationInterceptor via {} (fallback)", source);
        }
    }
}
