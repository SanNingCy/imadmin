package com.seekweb4.chat.agora.utils;

import io.agora.media.RtcTokenBuilder;
import io.agora.media.RtcTokenBuilder2;
import io.agora.media.RtcTokenBuilder2.Role;
import io.agora.rtm.RtmTokenBuilder;
import io.agora.rtm.RtmTokenBuilder2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Token生成工具类
 * 
 * <p>该工具类封装了Agora平台各种Token的生成逻辑，提供统一的Token生成接口。</p>
 * <p>支持RTC（实时音视频通信）和RTM（实时消息）两种类型的Token生成。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>生成RTC Token - 用于音视频通话、直播等场景</li>
 *   <li>生成RTC Token006 - 兼容旧版本的RTC Token</li>
 *   <li>生成RTM Token - 用于实时消息传输</li>
 *   <li>生成RTM Token006 - 兼容旧版本的RTM Token</li>
 * </ul>
 * 
 * <p><strong>Token类型说明：</strong></p>
 * <ul>
 *   <li><strong>RTC Token：</strong>Real-Time Communication，实时音视频通信认证令牌</li>
 *   <li><strong>RTM Token：</strong>Real-Time Messaging，实时消息传输认证令牌</li>
 *   <li><strong>Token006：</strong>旧版本Token格式，用于向下兼容</li>
 * </ul>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * {@code
 * @Autowired
 * private TokenUtil tokenUtil;
 * 
 * // 生成RTC Token
 * String rtcToken = tokenUtil.generateRtcToken(
 *     "your_app_id", 
 *     "your_app_cert", 
 *     "channel_name", 
 *     "user_account", 
 *     3600, 3600
 * );
 * 
 * // 生成RTM Token
 * String rtmToken = tokenUtil.generateRtmToken(
 *     "your_app_id", 
 *     "your_app_cert", 
 *     "user_id", 
 *     3600
 * );
 * }
 * </pre>
 * 
 * <p><strong>安全注意事项：</strong></p>
 * <ul>
 *   <li>AppCertificate是敏感信息，不应暴露在客户端代码中</li>
 *   <li>Token具有时效性，建议设置合理的过期时间</li>
 *   <li>不同业务场景应使用对应类型的Token</li>
 *   <li>定期更新Token以确保安全性</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 * @see RtcTokenBuilder2
 * @see RtcTokenBuilder
 * @see RtmTokenBuilder2
 * @see RtmTokenBuilder
 */
@Slf4j
@Component
public class TokenUtil {
    
    /**
     * RTC Token构建器（最新版本），用于生成实时音视频通信Token
     */
    private RtcTokenBuilder2 rtcTokenBuilder;
    
    /**
     * RTC Token构建器（006版本），用于向下兼容旧版本
     */
    private RtcTokenBuilder rtcToken006Builder;
    
    /**
     * RTM Token构建器（最新版本），用于生成实时消息Token
     */
    private RtmTokenBuilder2 rtmTokenBuilder;
    
    /**
     * RTM Token构建器（006版本），用于向下兼容旧版本
     */
    private RtmTokenBuilder rtmToken006Builder;

    /**
     * 构造函数 - 初始化各种Token构建器实例
     * 
     * <p>在Spring容器初始化时自动创建各种Token构建器的实例，
     * 为后续Token生成操作做准备。</p>
     */
    public TokenUtil() {
        rtcTokenBuilder = new RtcTokenBuilder2();
        rtcToken006Builder = new RtcTokenBuilder();
        rtmTokenBuilder = new RtmTokenBuilder2();
        rtmToken006Builder = new RtmTokenBuilder();
    }

    /**
     * 生成RTC Token（最新版本）
     * 
     * <p>生成用于实时音视频通信的最新版本Token，支持更丰富的功能和更强的安全性。</p>
     * 
     * <p><strong>适用场景：</strong></p>
     * <ul>
     *   <li>1对1音视频通话</li>
     *   <li>多人视频会议</li>
     *   <li>直播推拉流</li>
     *   <li>屏幕共享</li>
     *   <li>云端录制</li>
     * </ul>
     * 
     * <p><strong>权限说明：</strong></p>
     * <ul>
     *   <li>ROLE_PUBLISHER：可以发布和订阅音视频流</li>
     *   <li>适用于大多数音视频通信场景</li>
     * </ul>
     * 
     * @param appId 应用ID，在Agora控制台创建项目时获得
     * @param appCertificate 应用证书，用于Token签名验证，在Agora控制台获得
     * @param channelName 频道名称，同一频道内的用户可以互相通信
     * @param account 用户账号，频道内的唯一标识
     * @param tokenExpirationInSeconds Token过期时间（秒），建议设置为3600秒（1小时）
     * @param privilegeExpirationInSeconds 权限过期时间（秒），通常与Token过期时间相同
     * @return 生成的RTC Token字符串
     */
    public String generateRtcToken(String appId, String appCertificate, String channelName, String account,
            int tokenExpirationInSeconds, int privilegeExpirationInSeconds) {
        log.info(
                "generateRtcToken, appId:{}, channelName:{}, account:{}, tokenExpirationInSeconds:{}, privilegeExpirationInSeconds:{}",
                appId, channelName, account, tokenExpirationInSeconds, privilegeExpirationInSeconds);

        String token = rtcTokenBuilder.buildTokenWithUserAccount(appId,
                appCertificate, channelName, account,
                Role.ROLE_PUBLISHER, tokenExpirationInSeconds, privilegeExpirationInSeconds);
        return token;
    }

    /**
     * 生成RTC Token（006兼容版本）
     * 
     * <p>生成兼容旧版本SDK的RTC Token，主要用于向下兼容。</p>
     * <p>新项目建议使用最新版本的Token生成方法。</p>
     * 
     * <p><strong>兼容性：</strong></p>
     * <ul>
     *   <li>支持较旧版本的Agora SDK</li>
     *   <li>Token格式与新版本略有差异</li>
     *   <li>功能相对有限</li>
     * </ul>
     * 
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>需要兼容旧版本SDK的项目</li>
     *   <li>渐进式升级过程中的过渡方案</li>
     * </ul>
     * 
     * @param appId 应用ID
     * @param appCertificate 应用证书
     * @param channelName 频道名称
     * @param account 用户账号
     * @param privilegeExpirationInSeconds 权限过期时间（秒）
     * @return 生成的RTC Token006字符串
     */
    public String generateRtcToken006(String appId, String appCertificate, String channelName, String account,
            int privilegeExpirationInSeconds) {
        log.info(
                "generateRtcToken006, appId:{}, channelName:{}, account:{}, privilegeExpirationInSeconds:{}",
                appId, channelName, account, privilegeExpirationInSeconds);

        String token = rtcToken006Builder.buildTokenWithUserAccount(appId, appCertificate, channelName, account,
                RtcTokenBuilder.Role.Role_Publisher,
                (int) (System.currentTimeMillis() / 1000 + privilegeExpirationInSeconds));
        return token;
    }

    /**
     * 生成RTM Token（最新版本）
     * 
     * <p>生成用于实时消息传输的最新版本Token，支持各种即时通讯功能。</p>
     * 
     * <p><strong>RTM功能支持：</strong></p>
     * <ul>
     *   <li>点对点消息传输</li>
     *   <li>频道消息广播</li>
     *   <li>用户状态管理</li>
     *   <li>频道成员管理</li>
     *   <li>历史消息查询</li>
     * </ul>
     * 
     * <p><strong>应用场景：</strong></p>
     * <ul>
     *   <li>聊天室功能</li>
     *   <li>直播弹幕</li>
     *   <li>协作白板</li>
     *   <li>游戏内聊天</li>
     *   <li>客服系统</li>
     * </ul>
     * 
     * @param appId 应用ID
     * @param appCertificate 应用证书
     * @param userId 用户ID，RTM系统中的唯一标识
     * @param tokenExpirationInSeconds Token过期时间（秒）
     * @return 生成的RTM Token字符串
     */
    public String generateRtmToken(String appId, String appCertificate, String userId, int tokenExpirationInSeconds) {
        log.info("generateRtmToken, appId:{}, userId:{}, tokenExpirationInSeconds:{}", appId, userId,
                tokenExpirationInSeconds);

        String token = rtmTokenBuilder.buildToken(appId, appCertificate, userId,
                tokenExpirationInSeconds);
        return token;
    }

    /**
     * 生成RTM Token（006兼容版本）
     * 
     * <p>生成兼容旧版本SDK的RTM Token，主要用于向下兼容。</p>
     * <p>新项目建议使用最新版本的RTM Token生成方法。</p>
     * 
     * <p><strong>兼容性说明：</strong></p>
     * <ul>
     *   <li>支持旧版本的Agora RTM SDK</li>
     *   <li>权限模型相对简单</li>
     *   <li>适用于基础的消息传输场景</li>
     * </ul>
     * 
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>该方法可能抛出异常，需要适当的异常处理</li>
     *   <li>过期时间计算使用当前系统时间</li>
     * </ul>
     * 
     * @param appId 应用ID
     * @param appCertificate 应用证书
     * @param userId 用户ID
     * @param tokenExpirationInSeconds Token过期时间（秒）
     * @return 生成的RTM Token006字符串
     * @throws Exception 当Token生成过程中出现异常时抛出，如参数无效、证书错误等
     */
    public String generateRtmToken006(String appId, String appCertificate, String userId, int tokenExpirationInSeconds)
            throws Exception {
        log.info("generateRtmToken006, appId:{}, userId:{}, tokenExpirationInSeconds:{}", appId, userId,
                tokenExpirationInSeconds);

        String token = rtmToken006Builder.buildToken(appId, appCertificate, userId,
                RtmTokenBuilder.Role.Rtm_User, (int) (System.currentTimeMillis() / 1000 + tokenExpirationInSeconds));
        return token;
    }
}
