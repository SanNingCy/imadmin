package com.seekweb4.chat.config.shiro;

import com.seekweb4.chat.config.shiro.redis.RedisCacheManager;
import com.seekweb4.chat.core.security.shiro.session.CacheSessionDAO;
import com.seekweb4.chat.core.security.shiro.session.SessionManager;
import com.seekweb4.chat.modules.sys.security.KickoutSessionControlFilter;
import com.seekweb4.chat.modules.sys.security.SystemAuthorizingRealm;
import com.seekweb4.chat.modules.sys.security.shiro.JWTFilter;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import jakarta.servlet.Filter;
import java.util.*;

/**
 * shiro的控制类
 * Created by lixin
 */
@Configuration
@ConditionalOnProperty(name = "im.shiro.enabled", havingValue = "true", matchIfMissing = true)
public class ImShiroConfig {
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 并发登录控制
     * @return
     */
    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter(SessionManager sessionManager, CacheManager cacheManager){
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionControlFilter.setSessionManager(sessionManager);
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        kickoutSessionControlFilter.setCacheManager(cacheManager);
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
        kickoutSessionControlFilter.setKickoutAfter(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        kickoutSessionControlFilter.setMaxSession(1);
        return kickoutSessionControlFilter;
    }


    @Bean("shiroFilter")
    public ShiroFilterFactoryBean factory(KickoutSessionControlFilter kickoutSessionControlFilter, DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JWTFilter());
        filterMap.put("kickout", kickoutSessionControlFilter);
        factoryBean.setFilters(filterMap);

        factoryBean.setSecurityManager(securityManager);
        factoryBean.setUnauthorizedUrl("/401");

        /*
         * 自定义url规则
         */
        Map<String, String> filterRuleMap = new LinkedHashMap<>();

        // 访问401和404页面不通过我们的Filter
        filterRuleMap.put("/api/**", "anon"); //免验证范围太大
        filterRuleMap.put("/api/shua", "anon");
        filterRuleMap.put("/api/ptConfig", "anon");
        filterRuleMap.put("/api/versionUpdate", "anon");
        filterRuleMap.put("/api/getAccCode", "anon");
        filterRuleMap.put("/api/zhaohui", "anon");
        filterRuleMap.put("/api/getCode", "anon");
        filterRuleMap.put("/api/accLogin", "anon");
        filterRuleMap.put("/api/idLogin", "anon");
        filterRuleMap.put("/api/regist", "anon");
        filterRuleMap.put("/api/eqLogin", "anon");

        filterRuleMap.put("/roomAdmin/duration/config/**", "anon");
        filterRuleMap.put("/roomAdmin/meetingRoom/**", "anon");
        filterRuleMap.put("/roomAdmin/meeting/**", "anon");
        filterRuleMap.put("/roomAdmin/**", "anon");
        filterRuleMap.put("/meetings/**", "anon");
        filterRuleMap.put("/display/**", "anon");
        filterRuleMap.put("/webhook/**", "anon");
        filterRuleMap.put("/admin/gift/**", "anon");
        filterRuleMap.put("/gift/**", "anon");
        filterRuleMap.put("/crypto/**", "anon");
        filterRuleMap.put("/401", "anon");
        filterRuleMap.put("/402/1", "anon");
        filterRuleMap.put("/402/2", "anon");
        filterRuleMap.put("/403/1", "anon");
        filterRuleMap.put("/403/2", "anon");
        filterRuleMap.put("/404", "anon");
        filterRuleMap.put("/app/rest/**", "anon");
        filterRuleMap.put("/static/**","anon");
        filterRuleMap.put("/druid/**", "user");
        filterRuleMap.put("/doc.html", "anon");
        filterRuleMap.put("/swagger-ui.html", "anon");
        filterRuleMap.put("/swagger**/**", "anon");
        filterRuleMap.put("/webjars/**", "anon");
        filterRuleMap.put("/v1/**", "anon");
        filterRuleMap.put("/v2/**", "anon");
        filterRuleMap.put("/weboffice/**", "anon");
        filterRuleMap.put("/userfiles/**", "anon");
        filterRuleMap.put("/ReportServer/**", "anon");
        filterRuleMap.put("/sys/login", "anon");
        filterRuleMap.put("/sys/getCode", "anon");
        filterRuleMap.put("/app/sys/login", "anon");
        filterRuleMap.put("/sys/refreshToken/**", "anon");
        filterRuleMap.put("/sys/sysConfig/getConfig", "anon");
        filterRuleMap.put("/sys/casLogin", "anon");
        filterRuleMap.put("/systemInfoSocketServer", "anon");
        // 若依框架静态资源与登录页（与 IM JWT 并存）
        filterRuleMap.put("/login", "anon");
        filterRuleMap.put("/register", "anon");
        filterRuleMap.put("/captcha/captchaImage**", "anon");
        filterRuleMap.put("/css/**", "anon");
        filterRuleMap.put("/js/**", "anon");
        filterRuleMap.put("/ajax/**", "anon");
        filterRuleMap.put("/fonts/**", "anon");
        filterRuleMap.put("/img/**", "anon");
        filterRuleMap.put("/ruoyi/**", "anon");
        filterRuleMap.put("/web4x/**", "anon");
        filterRuleMap.put("/favicon.ico**", "anon");
        filterRuleMap.put("/im/**", "anon");
        filterRuleMap.put("/index", "anon");
        filterRuleMap.put("/logout", "anon");
        // 所有请求通过我们自己的JWT Filter
        filterRuleMap.put("/**", "jwt,kickout");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }



    @Bean(name = "basicHttpAuthenticationFilter")
    public BasicHttpAuthenticationFilter casFilter() {
        BasicHttpAuthenticationFilter basicHttpAuthenticationFilter = new BasicHttpAuthenticationFilter();
        basicHttpAuthenticationFilter.setLoginUrl("/login");
        return basicHttpAuthenticationFilter;
    }

    @Bean
    public CacheManager shiroRedisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        return redisCacheManager;
    }

    @Bean(name = "sessionManager")
    public SessionManager sessionManager(CacheSessionDAO dao) {
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(dao);
        sessionManager.setGlobalSessionTimeout(86400000);
        sessionManager.setSessionValidationInterval(1800000);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookie(new SimpleCookie("wolfking.jeeplus.session.id"));
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(SystemAuthorizingRealm systemAuthorizingRealm, SessionManager sessionManager, CacheManager cacheManager) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setCacheManager(cacheManager);
//        defaultWebSecurityManager.setRealm(systemAuthorizingRealm);
		Collection<Realm> typeRealms = new ArrayList<>();
		typeRealms.add(systemAuthorizingRealm);
		defaultWebSecurityManager.setRealms(typeRealms);
        return defaultWebSecurityManager;
    }

    // TODO 在开发环境中禁用权限
    /**
     * 权限检查开关（开发阶段可关闭）
     * 在 application.yml 中配置：shiro.permission-check-enabled: false 即可禁用所有 @RequiresPermissions 注解
     */
    @Value("${shiro.permission-check-enabled:true}")
    private boolean permissionCheckEnabled;

    // TODO 在开发环境中禁用权限
    @Bean
    @ConditionalOnProperty(name = "shiro.permission-check-enabled", havingValue = "true", matchIfMissing = true)
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.setOrder(2);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

}
