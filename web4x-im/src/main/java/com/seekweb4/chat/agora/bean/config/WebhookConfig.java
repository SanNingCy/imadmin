package com.seekweb4.chat.agora.bean.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Webhook配置属性类
 * 
 * <p>用于管理声网RTC Webhook相关的配置参数。</p>
 * <p>通过Spring Boot的@ConfigurationProperties机制，自动绑定配置文件中的参数。</p>
 * 
 * <p><b>配置前缀：</b>agora.webhook</p>
 * 
 * <p><b>配置示例：</b></p>
 * <pre>{@code
 * agora:
 *   webhook:
 *     customer-key: "your-customer-key"
 *     signature-verification:
 *       enabled: true
 *       preferred-algorithm: "sha256"
 *     event-handling:
 *       async-processing: true
 *       core-pool-size: 2
 *       max-pool-size: 10
 *     cache-cleanup-interval: 3600000
 *     notice-id-cache-time: 86400000
 * }</pre>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "agora.webhook")
public class WebhookConfig {
    
    /**
     * 客户密钥
     * 
     * <p>声网为每个项目分配的唯一密钥，用于webhook消息的HMAC签名验证。</p>
     * <p>必须与声网控制台中配置的客户密钥保持一致。</p>
     * 
     * <p><b>安全要求：</b></p>
     * <ul>
     *   <li>密钥长度应至少32位</li>
     *   <li>密钥应妥善保管，不得泄露</li>
     *   <li>建议定期更换密钥</li>
     * </ul>
     */
    private String customerKey;
    
    /**
     * 签名验证配置
     */
    private SignatureVerification signatureVerification = new SignatureVerification();
    
    /**
     * 事件处理配置
     */
    private EventHandling eventHandling = new EventHandling();
    
    /**
     * 缓存清理间隔（毫秒）
     * 
     * <p>清理过期事件通知ID的时间间隔，默认1小时（3600000毫秒）。</p>
     * <p>用于控制内存使用，防止已处理事件ID缓存无限增长。</p>
     */
    private long cacheCleanupInterval = 3600000L;
    
    /**
     * 事件通知ID缓存保留时间（毫秒）
     * 
     * <p>已处理的事件通知ID在内存中的保留时间，默认24小时（86400000毫秒）。</p>
     * <p>用于去重处理，避免重复处理相同的事件通知。</p>
     */
    private long noticeIdCacheTime = 86400000L;
    
    /**
     * 签名验证配置类
     * 
     * <p>控制webhook消息签名验证的相关参数。</p>
     */
    @Data
    public static class SignatureVerification {
        
        /**
         * 是否启用签名验证
         * 
         * <p>默认启用以确保webhook消息的安全性。</p>
         * 
         * <p><b>建议：</b></p>
         * <ul>
         *   <li>生产环境必须启用</li>
         *   <li>测试环境可以临时禁用以便调试</li>
         * </ul>
         */
        private boolean enabled = true;
        
        /**
         * 优先使用的签名算法
         * 
         * <p>当同时存在SHA1和SHA256签名时，优先验证的算法。</p>
         * 
         * <p><b>可选值：</b></p>
         * <ul>
         *   <li>"sha1" - 优先验证HMAC-SHA1签名</li>
         *   <li>"sha256" - 优先验证HMAC-SHA256签名</li>
         *   <li>"auto" - 自动选择（优先SHA256）</li>
         * </ul>
         */
        private String preferredAlgorithm = "auto";
        
        /**
         * 严格模式
         * 
         * <p>在严格模式下，必须同时验证SHA1和SHA256签名都通过。</p>
         * <p>默认为false，只要任意一个签名验证通过即可。</p>
         */
        private boolean strictMode = false;
    }
    
    /**
     * 事件处理配置类
     * 
     * <p>控制webhook事件处理的相关参数。</p>
     */
    @Data
    public static class EventHandling {
        
        /**
         * 是否启用异步处理
         * 
         * <p>启用异步处理可以快速响应webhook请求，避免阻塞声网服务器。</p>
         * <p>复杂的业务逻辑可以在后台异步执行。</p>
         */
        private boolean asyncProcessing = true;
        
        /**
         * 异步处理线程池核心大小
         * 
         * <p>异步处理事件的线程池核心线程数。</p>
         */
        private int corePoolSize = 2;
        
        /**
         * 异步处理线程池最大大小
         * 
         * <p>异步处理事件的线程池最大线程数。</p>
         */
        private int maxPoolSize = 10;
        
        /**
         * 队列容量
         * 
         * <p>异步处理队列的最大容量。</p>
         * <p>当队列满时，新的事件处理任务会被拒绝。</p>
         */
        private int queueCapacity = 100;
        
        /**
         * 线程名称前缀
         * 
         * <p>异步处理线程的名称前缀，便于监控和调试。</p>
         */
        private String threadNamePrefix = "webhook-event-";
        
        /**
         * 事件处理超时时间（秒）
         * 
         * <p>单个事件处理的最大允许时间，超时会被强制中断。</p>
         */
        private int timeoutSeconds = 30;
        
        /**
         * 失败重试次数
         * 
         * <p>事件处理失败时的最大重试次数。</p>
         */
        private int maxRetries = 3;
        
        /**
         * 重试间隔（毫秒）
         * 
         * <p>事件处理失败后的重试间隔时间。</p>
         */
        private long retryInterval = 1000L;
    }
    
    /**
     * 获取完整的客户密钥配置状态
     * 
     * @return true表示密钥已配置且不为空
     */
    public boolean isCustomerKeyConfigured() {
        return customerKey != null && !customerKey.trim().isEmpty();
    }
    
    /**
     * 验证配置的有效性
     * 
     * @return 配置验证结果描述
     */
    public String validateConfiguration() {
        StringBuilder issues = new StringBuilder();
        
        // 检查客户密钥
        if (signatureVerification.isEnabled() && !isCustomerKeyConfigured()) {
            issues.append("签名验证已启用但客户密钥未配置; ");
        }
        
        if (customerKey != null && customerKey.length() < 16) {
            issues.append("客户密钥长度过短，建议至少16位; ");
        }
        
        // 检查线程池配置
        if (eventHandling.getCorePoolSize() > eventHandling.getMaxPoolSize()) {
            issues.append("核心线程数不能大于最大线程数; ");
        }
        
        if (eventHandling.getQueueCapacity() <= 0) {
            issues.append("队列容量必须大于0; ");
        }
        
        // 检查时间配置
        if (cacheCleanupInterval <= 0) {
            issues.append("缓存清理间隔必须大于0; ");
        }
        
        if (noticeIdCacheTime <= 0) {
            issues.append("通知ID缓存时间必须大于0; ");
        }
        
        return issues.length() > 0 ? issues.toString() : "配置验证通过";
    }
}