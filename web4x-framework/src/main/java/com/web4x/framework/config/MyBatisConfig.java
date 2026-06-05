package com.web4x.framework.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import com.web4x.common.utils.StringUtils;

/**
 * Mybatis支持*匹配扫描包
 * 
 * @author web4x
 */
@Configuration
public class MyBatisConfig
{
    private static final Logger log = LoggerFactory.getLogger(MyBatisConfig.class);

    private static final String IM_PAGINATION_INTERCEPTOR =
            "com.seekweb4.chat.core.persistence.interceptor.PaginationInterceptor";

    @Autowired
    private Environment env;

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static String setTypeAliasesPackage(String typeAliasesPackage)
    {
        ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        List<String> allResult = new ArrayList<String>();
        try
        {
            for (String aliasesPackage : typeAliasesPackage.split(","))
            {
                List<String> result = new ArrayList<String>();
                aliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(aliasesPackage.trim()) + "/" + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = resolver.getResources(aliasesPackage);
                if (resources != null && resources.length > 0)
                {
                    MetadataReader metadataReader = null;
                    for (Resource resource : resources)
                    {
                        if (resource.isReadable())
                        {
                            metadataReader = metadataReaderFactory.getMetadataReader(resource);
                            try
                            {
                                result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                            }
                            catch (ClassNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (result.size() > 0)
                {
                    HashSet<String> hashResult = new HashSet<String>(result);
                    allResult.addAll(hashResult);
                }
            }
            if (allResult.size() > 0)
            {
                typeAliasesPackage = String.join(",", (String[]) allResult.toArray(new String[0]));
            }
            else
            {
                throw new RuntimeException("mybatis typeAliasesPackage 路径扫描错误,参数typeAliasesPackage:" + typeAliasesPackage + "未找到任何包");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return typeAliasesPackage;
    }

    public Resource[] resolveMapperLocations(String[] mapperLocations)
    {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (mapperLocations != null)
        {
            for (String mapperLocation : mapperLocations)
            {
                try
                {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
    {
        String typeAliasesPackage = resolveTypeAliasesPackage();
        String mapperLocations = resolveMapperLocationsProperty();
        String configLocation = resolveConfigLocation();
        typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage);
        VFS.addImplClass(SpringBootVFS.class);

        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sessionFactory.setMapperLocations(resolveMapperLocations(StringUtils.split(mapperLocations, ",")));
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
        Interceptor imPaginationInterceptor = resolveImPaginationInterceptor();
        if (imPaginationInterceptor != null)
        {
            sessionFactory.setPlugins(new Interceptor[] { imPaginationInterceptor });
        }
        SqlSessionFactory factory = sessionFactory.getObject();
        ensureImPaginationInterceptor(factory.getConfiguration());
        logImInterceptors(factory.getConfiguration());
        return factory;
    }

    private Interceptor resolveImPaginationInterceptor()
    {
        try
        {
            Class<?> clazz = Class.forName(IM_PAGINATION_INTERCEPTOR);
            return (Interceptor) clazz.getDeclaredConstructor().newInstance();
        }
        catch (ReflectiveOperationException ex)
        {
            return null;
        }
    }

    private void ensureImPaginationInterceptor(org.apache.ibatis.session.Configuration configuration)
    {
        boolean hasPagination = configuration.getInterceptors().stream()
                .anyMatch(interceptor -> IM_PAGINATION_INTERCEPTOR.equals(interceptor.getClass().getName()));
        if (!hasPagination)
        {
            Interceptor interceptor = resolveImPaginationInterceptor();
            if (interceptor != null)
            {
                configuration.addInterceptor(interceptor);
                log.info("Registered IM PaginationInterceptor on SqlSessionFactory configuration");
            }
        }
    }

    private void logImInterceptors(org.apache.ibatis.session.Configuration configuration)
    {
        if (configuration.getInterceptors().isEmpty())
        {
            log.warn("SqlSessionFactory has no MyBatis interceptors; IM list pagination will not work");
            return;
        }
        for (Interceptor interceptor : configuration.getInterceptors())
        {
            log.info("SqlSessionFactory interceptor: {}", interceptor.getClass().getName());
        }
    }

    /**
     * 兼容若依 camelCase 与 IM kebab-case（type-aliases-package），并合并双栈包路径。
     */
    private String resolveTypeAliasesPackage()
    {
        String packages = firstNonBlank(
                env.getProperty("mybatis.typeAliasesPackage"),
                env.getProperty("mybatis.type-aliases-package"));
        if (StringUtils.isEmpty(packages))
        {
            packages = "com.web4x.**.domain,com.seekweb4.chat.modules";
        }
        else if (!packages.contains("com.web4x"))
        {
            packages = "com.web4x.**.domain," + packages;
        }
        return packages;
    }

    private String resolveMapperLocationsProperty()
    {
        String locations = firstNonBlank(
                env.getProperty("mybatis.mapperLocations"),
                joinIndexedProperties("mybatis.mapper-locations"));
        if (StringUtils.isEmpty(locations))
        {
            locations = "classpath*:mapper/**/*Mapper.xml,classpath*:com/seekweb4/chat/modules/**/*Mapper.xml";
        }
        else if (!locations.contains("mapper/"))
        {
            locations = locations + ",classpath*:mapper/**/*Mapper.xml";
        }
        return locations;
    }

    private String resolveConfigLocation()
    {
        return firstNonBlank(
                env.getProperty("mybatis.configLocation"),
                env.getProperty("mybatis.config-location"),
                "classpath:mybatis/mybatis-config.xml");
    }

    private String joinIndexedProperties(String prefix)
    {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < 32; i++)
        {
            String value = env.getProperty(prefix + "[" + i + "]");
            if (StringUtils.isEmpty(value))
            {
                break;
            }
            values.add(value.trim());
        }
        if (values.isEmpty())
        {
            return env.getProperty(prefix);
        }
        return String.join(",", values);
    }

    private static String firstNonBlank(String... candidates)
    {
        for (String candidate : candidates)
        {
            if (StringUtils.isNotEmpty(candidate))
            {
                return candidate.trim();
            }
        }
        return null;
    }
}