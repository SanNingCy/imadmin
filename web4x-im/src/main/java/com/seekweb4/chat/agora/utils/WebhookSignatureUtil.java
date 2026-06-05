package com.seekweb4.chat.agora.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Webhook签名验证工具类
 * 
 * <p>提供声网Webhook消息签名的生成和验证功能，确保webhook消息的安全性和完整性。</p>
 * <p>声网支持两种签名算法：HMAC-SHA1和HMAC-SHA256，推荐使用更安全的SHA256算法。</p>
 * 
 * <p><b>签名验证流程：</b></p>
 * <ol>
 *   <li>声网服务器使用客户密钥对请求体进行HMAC签名</li>
 *   <li>将签名值放入HTTP请求头中发送</li>
 *   <li>业务服务器收到请求后，使用相同算法和密钥验证签名</li>
 *   <li>签名匹配则说明消息来源可信且未被篡改</li>
 * </ol>
 * 
 * <p><b>安全说明：</b></p>
 * <ul>
 *   <li>客户密钥必须妥善保管，不得泄露</li>
 *   <li>建议定期更换客户密钥</li>
 *   <li>生产环境必须启用签名验证</li>
 * </ul>
 * 
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 验证webhook签名
 * String customerKey = "your-customer-key";
 * String requestBody = "{\"noticeId\":\"...\"}";
 * String signature = request.getHeader("Agora-Signature-V2");
 * 
 * boolean isValid = WebhookSignatureUtil.verifyHmacSha256Signature(
 *     customerKey, requestBody, signature);
 * 
 * if (!isValid) {
 *     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
 * }
 * }</pre>
 * 
 * @author Agora
 * @version 1.0
 * @see <a href="https://docs.agora.io/cn/Interactive%20Broadcast/rtc_channel_event#notification-authentication">Webhook认证</a>
 */
@Slf4j
public class WebhookSignatureUtil {
    
    /**
     * HMAC-SHA1算法名称
     */
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    /**
     * HMAC-SHA256算法名称
     */
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    
    /**
     * 验证HMAC-SHA1签名
     * 
     * <p>验证使用HMAC-SHA1算法生成的签名是否正确。</p>
     * <p>对应声网webhook请求头中的"Agora-Signature"字段。</p>
     * 
     * <p><b>注意：</b>SHA1算法相对较旧，建议优先使用SHA256算法。</p>
     * 
     * @param customerKey 声网客户密钥，用于签名计算
     * @param requestBody webhook请求体的原始JSON字符串
     * @param signature 待验证的签名值，来自请求头"Agora-Signature"
     * @return true表示签名验证通过，false表示验证失败
     * 
     * @throws IllegalArgumentException 当参数为null时抛出
     */
    public static boolean verifyHmacSha1Signature(String customerKey, String requestBody, String signature) {
        if (customerKey == null || requestBody == null || signature == null) {
            log.warn("HMAC-SHA1签名验证参数不完整: customerKey={}, requestBody={}, signature={}", 
                     customerKey != null, requestBody != null, signature != null);
            return false;
        }
        
        try {
            String calculatedSignature = generateHmacSha1Signature(customerKey, requestBody);
            boolean result = signature.equals(calculatedSignature);
            
            if (!result) {
                log.warn("HMAC-SHA1签名验证失败: expected={}, actual={}", calculatedSignature, signature);
            }
            
            return result;
        } catch (Exception e) {
            log.error("HMAC-SHA1签名验证异常", e);
            return false;
        }
    }
    
    /**
     * 验证HMAC-SHA256签名
     * 
     * <p>验证使用HMAC-SHA256算法生成的签名是否正确。</p>
     * <p>对应声网webhook请求头中的"Agora-Signature-V2"字段。</p>
     * 
     * <p><b>推荐：</b>SHA256算法安全性更高，推荐在生产环境中使用。</p>
     * 
     * @param customerKey 声网客户密钥，用于签名计算
     * @param requestBody webhook请求体的原始JSON字符串
     * @param signature 待验证的签名值，来自请求头"Agora-Signature-V2"
     * @return true表示签名验证通过，false表示验证失败
     * 
     * @throws IllegalArgumentException 当参数为null时抛出
     */
    public static boolean verifyHmacSha256Signature(String customerKey, String requestBody, String signature) {
        if (customerKey == null || requestBody == null || signature == null) {
            log.warn("HMAC-SHA256签名验证参数不完整: customerKey={}, requestBody={}, signature={}", 
                     customerKey != null, requestBody != null, signature != null);
            return false;
        }
        
        try {
            String calculatedSignature = generateHmacSha256Signature(customerKey, requestBody);
            boolean result = signature.equals(calculatedSignature);
            
            if (!result) {
                log.warn("HMAC-SHA256签名验证失败: expected={}, actual={}", calculatedSignature, signature);
            }
            
            return result;
        } catch (Exception e) {
            log.error("HMAC-SHA256签名验证异常", e);
            return false;
        }
    }
    
    /**
     * 生成HMAC-SHA1签名
     * 
     * <p>使用HMAC-SHA1算法生成消息签名。</p>
     * <p>主要用于测试和调试场景。</p>
     * 
     * @param customerKey 客户密钥
     * @param requestBody 请求体内容
     * @return Base64编码的签名值
     * 
     * @throws NoSuchAlgorithmException 当系统不支持HMAC-SHA1算法时抛出
     * @throws InvalidKeyException 当提供的密钥无效时抛出
     */
    public static String generateHmacSha1Signature(String customerKey, String requestBody) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                customerKey.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA1_ALGORITHM
        );
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(hash);
        
        log.debug("生成HMAC-SHA1签名: signature={}", signature);
        return signature;
    }
    
    /**
     * 生成HMAC-SHA256签名
     * 
     * <p>使用HMAC-SHA256算法生成消息签名。</p>
     * <p>主要用于测试和调试场景。</p>
     * 
     * @param customerKey 客户密钥
     * @param requestBody 请求体内容
     * @return Base64编码的签名值
     * 
     * @throws NoSuchAlgorithmException 当系统不支持HMAC-SHA256算法时抛出
     * @throws InvalidKeyException 当提供的密钥无效时抛出
     */
    public static String generateHmacSha256Signature(String customerKey, String requestBody) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                customerKey.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA256_ALGORITHM
        );
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(hash);
        
        log.debug("生成HMAC-SHA256签名: signature={}", signature);
        return signature;
    }
    
    /**
     * 智能签名验证
     * 
     * <p>自动选择合适的签名算法进行验证：</p>
     * <ul>
     *   <li>优先验证SHA256签名（Agora-Signature-V2）</li>
     *   <li>如果SHA256签名不存在或验证失败，则尝试SHA1签名（Agora-Signature）</li>
     *   <li>任意一个签名验证通过即认为消息有效</li>
     * </ul>
     * 
     * @param customerKey 客户密钥
     * @param requestBody 请求体内容
     * @param sha1Signature SHA1签名值，可以为null
     * @param sha256Signature SHA256签名值，可以为null
     * @return true表示至少一个签名验证通过
     */
    public static boolean verifySignature(String customerKey, String requestBody, 
                                        String sha1Signature, String sha256Signature) {
        
        if (customerKey == null || requestBody == null) {
            log.warn("智能签名验证参数不完整");
            return false;
        }
        
        boolean sha256Valid = false;
        boolean sha1Valid = false;
        
        // 优先验证SHA256签名
        if (sha256Signature != null) {
            sha256Valid = verifyHmacSha256Signature(customerKey, requestBody, sha256Signature);
            log.debug("SHA256签名验证结果: {}", sha256Valid);
        }
        
        // 如果SHA256验证失败，尝试SHA1签名
        if (!sha256Valid && sha1Signature != null) {
            sha1Valid = verifyHmacSha1Signature(customerKey, requestBody, sha1Signature);
            log.debug("SHA1签名验证结果: {}", sha1Valid);
        }
        
        boolean result = sha256Valid || sha1Valid;
        log.debug("智能签名验证最终结果: {} (SHA256: {}, SHA1: {})", result, sha256Valid, sha1Valid);
        
        return result;
    }
}