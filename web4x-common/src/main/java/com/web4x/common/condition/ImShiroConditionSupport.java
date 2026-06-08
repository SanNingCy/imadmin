package com.web4x.common.condition;

import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * 兼容 Spring Boot 4 Config Data 的 {@link OriginTrackedValue}，
 * 供自定义 Condition 替代 {@code @ConditionalOnProperty(im.shiro.enabled)}。
 */
public final class ImShiroConditionSupport
{
    public static final String PROPERTY = "im.shiro.enabled";

    private ImShiroConditionSupport()
    {
    }

    public static boolean isImShiroEnabled(Environment environment)
    {
        String value = resolveProperty(environment, PROPERTY);
        if (value == null)
        {
            return false;
        }
        return "true".equalsIgnoreCase(value);
    }

    public static boolean isImShiroExplicitlyDisabled(Environment environment)
    {
        String value = resolveProperty(environment, PROPERTY);
        return value != null && "false".equalsIgnoreCase(value);
    }

    public static String resolveProperty(Environment environment, String key)
    {
        if (environment instanceof ConfigurableEnvironment configurable)
        {
            for (PropertySource<?> source : configurable.getPropertySources())
            {
                if (!source.containsProperty(key))
                {
                    continue;
                }
                return toStringValue(unwrap(source.getProperty(key)));
            }
            return null;
        }
        return environment.getProperty(key);
    }

    static Object unwrap(Object raw)
    {
        if (raw instanceof OriginTrackedValue tracked)
        {
            return tracked.getValue();
        }
        return raw;
    }

    static String toStringValue(Object value)
    {
        return value == null ? null : String.valueOf(value);
    }
}
