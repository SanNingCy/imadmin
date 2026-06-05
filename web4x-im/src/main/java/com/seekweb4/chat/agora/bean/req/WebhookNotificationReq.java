package com.seekweb4.chat.agora.bean.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 声网RTC Webhook通知请求对象
 * 
 * <p>用于接收声网服务器发送的RTC频道事件通知。声网服务器在特定频道事件发生时，
 * 会向配置的webhook URL发送HTTP POST请求，请求体包含事件的详细信息。</p>
 * 
 * <p><b>主要用途：</b></p>
 * <ul>
 *   <li><b>频道事件监听</b> - 监听用户加入/离开频道事件</li>
 *   <li><b>用户状态跟踪</b> - 跟踪用户角色变化、发流状态变化</li>
 *   <li><b>频道统计</b> - 实时统计频道在线人数、通话时长等</li>
 *   <li><b>业务触发</b> - 基于频道事件触发业务逻辑</li>
 * </ul>
 * 
 * <p><b>常见事件类型：</b></p>
 * <ul>
 *   <li>101 - 主播加入频道</li>
 *   <li>102 - 主播离开频道</li>
 *   <li>103 - 观众加入频道</li>
 *   <li>104 - 观众离开频道</li>
 *   <li>111 - 用户角色从观众切换到主播</li>
 *   <li>112 - 用户角色从主播切换到观众</li>
 * </ul>
 * 
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 处理用户加入频道事件
 * if (request.getEventType() == 101) {
 *     // 解析payload获取用户信息
 *     Map<String, Object> payload = (Map<String, Object>) request.getPayload();
 *     String channelName = (String) payload.get("channelName");
 *     String uid = (String) payload.get("uid");
 *     
 *     // 执行业务逻辑
 *     userJoinChannelHandler.handle(channelName, uid);
 * }
 * }</pre>
 * 
 * @author Agora
 * @version 1.0
 * @see <a href="https://docs.agora.io/cn/Interactive%20Broadcast/rtc_channel_event">RTC频道事件</a>
 */
@Data
public class WebhookNotificationReq {
    
    /**
     * 通知ID
     * 
     * <p>由声网业务服务器生成的唯一标识符，用于标识本次事件通知。</p>
     * <p>可用于去重处理，避免重复处理同一个事件。</p>
     * 
     * <p><b>格式：</b>UUID字符串</p>
     * <p><b>示例：</b>"550e8400-e29b-41d4-a716-446655440000"</p>
     */
    @JsonProperty("noticeId")
    @NotNull(message = "通知ID不能为空")
    private String noticeId;
    
    /**
     * 产品ID
     * 
     * <p>标识声网的具体业务产品类型。</p>
     * <ul>
     *   <li>1 - 实时音视频通信服务(RTC)</li>
     *   <li>2 - 实时消息服务(RTM)</li>
     *   <li>3 - 音视频录制服务</li>
     *   <li>4 - 云端录制服务</li>
     * </ul>
     * 
     * <p>对于RTC频道事件，此值通常为1。</p>
     */
    @JsonProperty("productId")
    @NotNull(message = "产品ID不能为空")
    private Integer productId;
    
    /**
     * 事件类型
     * 
     * <p>标识具体的频道事件类型，是webhook处理的核心判断依据。</p>
     * 
     * <p><b>直播场景事件类型：</b></p>
     * <ul>
     *   <li>101 - 主播加入频道</li>
     *   <li>102 - 主播离开频道</li>
     *   <li>103 - 观众加入频道</li>
     *   <li>104 - 观众离开频道</li>
     *   <li>111 - 用户角色从观众切换到主播</li>
     *   <li>112 - 用户角色从主播切换到观众</li>
     * </ul>
     * 
     * <p><b>通信场景事件类型：</b></p>
     * <ul>
     *   <li>201 - 用户加入频道</li>
     *   <li>202 - 用户离开频道</li>
     * </ul>
     */
    @JsonProperty("eventType")
    @NotNull(message = "事件类型不能为空")
    private Integer eventType;
    
    /**
     * 通知时间戳
     * 
     * <p>声网服务器向业务服务器发送事件通知的Unix时间戳（毫秒）。</p>
     * <p>可用于判断事件的时效性，过期事件可以选择忽略处理。</p>
     * 
     * <p><b>示例：</b>1640995200000 (对应 2022-01-01 08:00:00 UTC)</p>
     */
    @JsonProperty("notifyMs")
    @NotNull(message = "通知时间戳不能为空")
    private Long notifyMs;
    
    /**
     * 应用ID
     * 
     * <p>标识具体的Agora应用项目，用于区分不同的应用实例。</p>
     * <p>在多应用场景下，通过appId可以准确识别事件来源。</p>
     * 
     * <p><b>格式：</b>32位十六进制字符串</p>
     * <p><b>示例：</b>"d1f140de133c4508a532c0033840a801"</p>
     */
    @JsonProperty("appId")
    private String appId;
    
    /**
     * 频道名称
     * 
     * <p>发生事件的RTC频道名称。</p>
     * <p>用于标识具体的频道实例，同一个应用下可以有多个不同名称的频道。</p>
     * 
     * <p><b>示例：</b>"test_channel", "live_room_001"</p>
     */
    @JsonProperty("channelName")
    private String channelName;
    
    /**
     * 会话ID
     * 
     * <p>标识一次完整的RTC会话，从第一个用户加入频道到最后一个用户离开频道。</p>
     * <p>同一个会话中的所有事件都具有相同的sid，可用于关联和统计同一次会话的所有事件。</p>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>会话时长统计</li>
     *   <li>会话事件关联分析</li>
     *   <li>会话质量评估</li>
     * </ul>
     * 
     * <p><b>格式：</b>UUID字符串</p>
     */
    @JsonProperty("sid")
    private String sid;
    
    /**
     * 事件详细内容
     * 
     * <p>包含具体事件的详细信息，不同事件类型的payload结构不同。</p>
     * 
     * <p><b>通用字段（大部分事件都包含）：</b></p>
     * <ul>
     *   <li>channelName - 频道名称</li>
     *   <li>uid - 用户ID</li>
     *   <li>ts - 事件发生的时间戳</li>
     *   <li>platform - 用户使用的平台</li>
     * </ul>
     * 
     * <p><b>示例payload（用户加入频道事件）：</b></p>
     * <pre>{@code
     * {
     *   "channelName": "test_channel",
     *   "uid": "123456",
     *   "ts": 1640995200,
     *   "platform": "Android",
     *   "elapsed": 500
     * }
     * }</pre>
     * 
     * <p><b>注意：</b>由于不同事件类型的payload结构差异较大，
     * 使用Object类型存储，业务处理时需要根据eventType进行相应的类型转换。</p>
     */
    @JsonProperty("payload")
    private Object payload;
}