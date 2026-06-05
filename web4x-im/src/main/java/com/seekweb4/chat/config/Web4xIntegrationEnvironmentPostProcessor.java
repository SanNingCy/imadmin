package com.seekweb4.chat.config;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * 仅做 web4x 集成补充（Druid 桥接、若依 MyBatis 合并等），
 * 不读取 chat-ops 项目目录；业务配置以 web4x-admin/resources 下 yml 为准。
 */
public class Web4xIntegrationEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered
{
    private static final YamlPropertySourceLoader YAML_LOADER = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
    {
        Map<String, Object> integration = new LinkedHashMap<>();
        loadClasspathOverlay(integration, "application-web4x-ruoyi.yml");
        bridgeDruidFromFlatDatasource(environment, integration);
        mergeMybatisForRuoyiAndIm(integration);
        fillRuoyiDefaultsIfMissing(environment, integration);
        normalizeIntegrationValues(integration);
        if (!integration.isEmpty())
        {
            environment.getPropertySources().addFirst(new MapPropertySource("web4xIntegration", integration));
        }
    }

    /** 统一为字符串，避免 SB4 {@code @ConditionalOnProperty} 无法转换 OriginTrackedValue/Boolean。 */
    private static void normalizeIntegrationValues(Map<String, Object> integration)
    {
        integration.replaceAll((key, value) -> toPropertyString(value));
    }

    private static String toPropertyString(Object value)
    {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 将 overlay yml 扁平化为点分隔键写入 MapPropertySource，
     * 避免嵌套 Map 无法被 {@code environment.getProperty("user.password.maxRetryCount")} 解析。
     */
    private static void loadClasspathOverlay(Map<String, Object> target, String classpathLocation)
    {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        if (!resource.exists())
        {
            return;
        }
        try
        {
            List<PropertySource<?>> sources = YAML_LOADER.load("web4xRuoyiOverlay", resource);
            for (PropertySource<?> source : sources)
            {
                if (source.getSource() instanceof Map<?, ?> sourceMap)
                {
                    flattenYamlMap("", sourceMap, target);
                }
            }
        }
        catch (IOException ex)
        {
            throw new IllegalStateException("加载 " + classpathLocation + " 失败", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static void flattenYamlMap(String prefix, Map<?, ?> source, Map<String, Object> target)
    {
        for (Map.Entry<?, ?> entry : source.entrySet())
        {
            String key = prefix.isEmpty() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> nested)
            {
                flattenYamlMap(key, nested, target);
            }
            else if (value instanceof List<?> list)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    Object item = list.get(i);
                    String indexedKey = key + "[" + i + "]";
                    if (item instanceof Map<?, ?> nestedItem)
                    {
                        flattenYamlMap(indexedKey, nestedItem, target);
                    }
                    else if (item != null)
                    {
                        target.putIfAbsent(indexedKey, toPropertyString(item));
                    }
                }
            }
            else if (value != null)
            {
                target.putIfAbsent(key, toPropertyString(value));
            }
        }
    }

    private static void bridgeDruidFromFlatDatasource(ConfigurableEnvironment environment, Map<String, Object> target)
    {
        if (StringUtils.hasText(environment.getProperty("spring.datasource.druid.master.url")))
        {
            fillDruidPoolDefaults(environment, target);
            return;
        }
        String url = environment.getProperty("spring.datasource.url");
        if (!StringUtils.hasText(url))
        {
            fillDruidPoolDefaults(environment, target);
            return;
        }
        target.put("spring.datasource.druid.master.url", url);
        putIfPresent(target, environment, "spring.datasource.druid.master.username", "spring.datasource.username");
        putIfPresent(target, environment, "spring.datasource.druid.master.password", "spring.datasource.password");
        putIfPresent(target, environment, "spring.datasource.druid.master.driver-class-name", "spring.datasource.driver-class-name");
        fillDruidPoolDefaults(environment, target);
    }

    /**
     * IM 配置为 spring.datasource + Hikari 扁平写法时，为若依 DruidProperties 补全连接池参数（不改用户 yml）。
     */
    private static void fillDruidPoolDefaults(ConfigurableEnvironment environment, Map<String, Object> target)
    {
        putDefaultIfMissing(target, environment, "spring.datasource.druid.initialSize",
                firstProperty(environment, "spring.datasource.hikari.minimum-idle", "5"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.minIdle",
                firstProperty(environment, "spring.datasource.hikari.minimum-idle", "5"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.maxActive",
                firstProperty(environment, "spring.datasource.hikari.maximum-pool-size", "20"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.maxWait", "60000");
        putDefaultIfMissing(target, environment, "spring.datasource.druid.connectTimeout",
                firstProperty(environment, "spring.datasource.hikari.connection-timeout", "30000"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.socketTimeout", "60000");
        putDefaultIfMissing(target, environment, "spring.datasource.druid.timeBetweenEvictionRunsMillis", "60000");
        putDefaultIfMissing(target, environment, "spring.datasource.druid.minEvictableIdleTimeMillis",
                firstProperty(environment, "spring.datasource.hikari.idle-timeout", "300000"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.maxEvictableIdleTimeMillis",
                firstProperty(environment, "spring.datasource.hikari.max-lifetime", "900000"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.validationQuery",
                firstProperty(environment, "spring.datasource.hikari.connection-test-query", "SELECT 1 FROM DUAL"));
        putDefaultIfMissing(target, environment, "spring.datasource.druid.testWhileIdle", "true");
        putDefaultIfMissing(target, environment, "spring.datasource.druid.testOnBorrow", "false");
        putDefaultIfMissing(target, environment, "spring.datasource.druid.testOnReturn", "false");
        putDefaultIfMissing(target, environment, "spring.datasource.druid.slave.enabled", "false");
    }

    private static String firstProperty(ConfigurableEnvironment environment, String key, String defaultValue)
    {
        String value = environment.getProperty(key);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private static void mergeMybatisForRuoyiAndIm(Map<String, Object> target)
    {
        target.put("mybatis.typeAliasesPackage", "com.web4x.**.domain,com.seekweb4.chat.modules");
        target.put("mybatis.mapperLocations",
                "classpath*:mapper/**/*Mapper.xml,classpath*:com/seekweb4/chat/modules/**/*Mapper.xml");
        target.put("mybatis.configLocation", "classpath:mybatis/mybatis-config.xml");
    }

    private static void fillRuoyiDefaultsIfMissing(ConfigurableEnvironment environment, Map<String, Object> target)
    {
        putDefaultIfMissing(target, environment, "im.shiro.enabled", "true");
        putDefaultIfMissing(target, environment, "user.password.maxRetryCount", "5");
        putDefaultIfMissing(target, environment, "shiro.user.loginUrl", "/login");
        putDefaultIfMissing(target, environment, "shiro.user.unauthorizedUrl", "/unauth");
        putDefaultIfMissing(target, environment, "shiro.user.indexUrl", "/index");
        putDefaultIfMissing(target, environment, "shiro.user.captchaEnabled", "true");
        putDefaultIfMissing(target, environment, "shiro.user.captchaType", "math");
        putDefaultIfMissing(target, environment, "shiro.rememberMe.enabled", "true");
        putDefaultIfMissing(target, environment, "shiro.session.expireTime", "30");
        putDefaultIfMissing(target, environment, "shiro.session.validationInterval", "10");
        putDefaultIfMissing(target, environment, "shiro.session.maxSession", "-1");
        putDefaultIfMissing(target, environment, "shiro.session.kickoutAfter", "false");
        putDefaultIfMissing(target, environment, "shiro.session.dbSyncPeriod", "1");
        putDefaultIfMissing(target, environment, "shiro.cookie.path", "/");
        putDefaultIfMissing(target, environment, "shiro.cookie.httpOnly", "true");
        putDefaultIfMissing(target, environment, "shiro.cookie.maxAge", "30");
        putDefaultIfMissing(target, environment, "xss.enabled", "true");
        putDefaultIfMissing(target, environment, "xss.excludes", "/system/notice/*");
        putDefaultIfMissing(target, environment, "xss.urlPatterns", "/system/*,/monitor/*,/tool/*");
        putDefaultIfMissing(target, environment, "csrf.enabled", "false");
        putDefaultIfMissing(target, environment, "web4x.name", "Web4x");
        putDefaultIfMissing(target, environment, "web4x.version", "4.8.3");
        putDefaultIfMissing(target, environment, "web4x.demoEnabled", "false");
    }

    private static void putDefaultIfMissing(Map<String, Object> target, ConfigurableEnvironment environment,
            String key, String defaultValue)
    {
        if (!target.containsKey(key) && !StringUtils.hasText(environment.getProperty(key)))
        {
            target.put(key, defaultValue);
        }
    }

    private static void putIfPresent(Map<String, Object> target, ConfigurableEnvironment environment,
            String targetKey, String sourceKey)
    {
        String value = environment.getProperty(sourceKey);
        if (StringUtils.hasText(value))
        {
            target.put(targetKey, value);
        }
    }

    @Override
    public int getOrder()
    {
        // 所有 application*.yml 加载完成后再补充集成项
        return Ordered.LOWEST_PRECEDENCE;
    }
}
