package com.web4x.framework.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import com.alibaba.druid.spring.boot4.autoconfigure.stat.DruidStatViewServletConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot4.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.web4x.common.enums.DataSourceType;
import com.web4x.common.utils.spring.SpringUtils;
import com.web4x.framework.config.properties.DruidProperties;
import com.web4x.framework.datasource.DynamicDataSource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * druid 配置多数据源
 * 
 * @author web4x
 */
@Configuration
@Import(DruidStatViewServletConfiguration.class)
public class DruidConfig
{
    /** 排除 DataSourceAutoConfiguration 时需手动注册，供监控页 Filter 使用 */
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public DruidStatProperties druidStatProperties()
    {
        return new DruidStatProperties();
    }

    @Bean
    public DataSource masterDataSource(DruidProperties druidProperties, Environment environment)
    {
        DruidDataSource dataSource = new DruidDataSource();
        applyMasterConnectionProperties(dataSource, environment);
        return druidProperties.dataSource(dataSource);
    }

    /** IM 扁平 spring.datasource.* 与若依 druid.master.* 兼容，无需改业务 yml */
    private static void applyMasterConnectionProperties(DruidDataSource dataSource, Environment environment)
    {
        String url = firstProperty(environment, "spring.datasource.druid.master.url", "spring.datasource.url");
        if (StringUtils.hasText(url))
        {
            dataSource.setUrl(url);
        }
        String username = firstProperty(environment, "spring.datasource.druid.master.username", "spring.datasource.username");
        if (StringUtils.hasText(username))
        {
            dataSource.setUsername(username);
        }
        String password = firstProperty(environment, "spring.datasource.druid.master.password", "spring.datasource.password");
        if (StringUtils.hasText(password))
        {
            dataSource.setPassword(password);
        }
        String driver = firstProperty(environment, "spring.datasource.druid.master.driver-class-name",
                "spring.datasource.driver-class-name");
        if (StringUtils.hasText(driver))
        {
            dataSource.setDriverClassName(driver);
        }
    }

    private static String firstProperty(Environment environment, String... keys)
    {
        for (String key : keys)
        {
            String value = environment.getProperty(key);
            if (StringUtils.hasText(value))
            {
                return value;
            }
        }
        return null;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = new DruidDataSource();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

    /**
     * 设置数据源
     * 
     * @param targetDataSources 备选数据源集合
     * @param sourceName 数据源名称
     * @param beanName bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName)
    {
        try
        {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        }
        catch (Exception e)
        {
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties)
    {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        if (config == null)
        {
            config = new DruidStatProperties.StatViewServlet();
        }
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter()
        {
            @Override
            public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException
            {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException
            {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }

            @Override
            public void destroy()
            {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
