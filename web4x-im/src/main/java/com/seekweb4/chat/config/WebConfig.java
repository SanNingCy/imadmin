package com.seekweb4.chat.config;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson2.JSONArray;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.seekweb4.chat.LogInterceptor;
import com.seekweb4.chat.core.mapper.JsonMapper;
import com.seekweb4.chat.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * Created by lixinapp on 2017/9/28.
 */
//@ConditionalOnBean(ShiroConfig.class)
//    @EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DispatcherServlet dispatcherServlet;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //registry.addResourceHandler("/act/**").addResourceLocations("classpath:/act/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加移动端jwt校验拦截器
        String anon = ResourceUtil.readUtf8Str("anon.json");
        List<String> anonList = JSONArray.parseArray(anon, String.class);
        registry.addInterceptor(new JwtInterceptor()).addPathPatterns("/api/**").excludePathPatterns(anonList).order(0);
        //registry.addInterceptor(new JwtInterceptor()).addPathPatterns("/api/**").order(0);
        // 添加日志记录拦截器
        //registry.addInterceptor(new LogInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**").order(1);
    }

    @Bean
    public LogInterceptor logInterceptor(){
        return new LogInterceptor();
    }
    @Bean
    public ServletRegistrationBean apiServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        //注入上传配置到自己注册的ServletRegistrationBean
        bean.addUrlMappings("/service/*");
        bean.setName("ModelRestServlet");
        return bean;
    }
    @Bean
    public ServletRegistrationBean restServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        //注入上传配置到自己注册的ServletRegistrationBean
        bean.addUrlMappings("/rest/*");
        bean.setName("RestServlet");
        return bean;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<MediaType> supportedMediaTypes = Lists.newArrayList();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        //formHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(formHttpMessageConverter);

        StringHttpMessageConverter stringHttpMessageConverter =new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(stringHttpMessageConverter);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setPrettyPrint(false);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        mappingJackson2HttpMessageConverter.setObjectMapper(new JsonMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL));
        converters.add(mappingJackson2HttpMessageConverter);

        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> byteSupportedMediaTypes = Lists.newArrayList();
        byteSupportedMediaTypes.add(MediaType.ALL);
        byteSupportedMediaTypes.add(MediaType.IMAGE_PNG);
        byteSupportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        byteSupportedMediaTypes.add(MediaType.IMAGE_GIF);
        byteSupportedMediaTypes.add(MediaType.IMAGE_JPEG);
        byteSupportedMediaTypes.add(MediaType.valueOf("image/*"));
        byteArrayHttpMessageConverter.setSupportedMediaTypes(byteSupportedMediaTypes);
        converters.add(byteArrayHttpMessageConverter);
    }
//    如果使用这个的话addCorsMappings这个就可以注释了
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许携带凭证时，不能使用 *，需要明确域名白名单
        config.setAllowCredentials(true);
        // 允许的源域名（开发环境和生产环境）
//        config.setAllowedOrigins(java.util.Arrays.asList(
//                // 生产环境域名
//                "https://dev.seekweb4.com", // 前端
//                "https://api.seekweb4.com", // 后端
//                "https://seekweb4.com",
//                "http://119.45.9.228:84/lixin",
//                "http://119.45.9.228:84",
//                "http://119.45.9.228:82",
//                // 开发环境
//                "http://localhost:3000", // 前端
//                "http://127.0.0.1:3000", // 前端
//                "http://localhost:8082", // 后端
//                "http://127.0.0.1:8082", // 后端
//                "http://localhost:8080", // 后端
//                "http://127.0.0.1:8080", // 后端
//                "http://192.168.1.160:8082", // 后端
//                "http://192.168.1.3:3000" // 前端
//        ));
        config.setAllowedOriginPatterns(java.util.Arrays.asList("*"));
        // 允许的请求头
        config.setAllowedHeaders(java.util.Arrays.asList("*"));
        // 允许跨域请求通过的类型
        config.setAllowedMethods(java.util.Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","HEAD","PATCH"));
        config.setExposedHeaders(java.util.Arrays.asList("Set-Cookie","Authorization","Content-Type"));
        config.setMaxAge(3600L); // 缓存时间, 60分钟内的其他跨域请求不再校验
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

// 处理跨域问题CORS的问题
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }

    // CORS 由上方 corsFilter Bean 统一处理，避免与 allowedOrigins("*")+allowCredentials 冲突
}
