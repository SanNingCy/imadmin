package com.seekweb4.chat.agora.bean.req;

import lombok.Data;

import java.util.List;

/**
 * 创建封禁规则请求对象
 * 
 * <p>用于调用声网RTC API创建用户权限封禁规则的请求参数对象。</p>
 * <p>支持基于频道、用户ID、IP地址等多种条件创建封禁规则，可以封禁用户的加入频道、发布音频、发布视频等权限。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>多维度封禁</b> - 支持按频道名称、用户ID、IP地址进行封禁</li>
 *   <li><b>权限细分</b> - 可选择性封禁加入频道、发布音频、发布视频等权限</li>
 *   <li><b>时长控制</b> - 支持按分钟或秒设置封禁时长</li>
 *   <li><b>组合条件</b> - 可同时指定多个封禁条件</li>
 * </ul>
 * 
 * <p><b>封禁规则工作原理：</b></p>
 * <p>封禁规则基于三个字段的组合工作：cname（频道名称）、uid（用户ID）和ip（IP地址）。
 * 当用户的信息匹配规则中的条件时，相应的权限将被封禁。</p>
 * 
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 示例1：封禁特定用户在特定频道的发言权限
 * BanRuleCreateReq banReq = new BanRuleCreateReq();
 * banReq.setAppid("your-app-id");
 * banReq.setCname("meeting-room-001");
 * banReq.setUid(12345L);
 * banReq.setTimeInSeconds(3600); // 封禁1小时
 * banReq.setPrivileges(Arrays.asList("publish_audio", "publish_video"));
 * 
 * // 示例2：封禁特定IP地址加入所有频道
 * BanRuleCreateReq ipBanReq = new BanRuleCreateReq();
 * ipBanReq.setAppid("your-app-id");
 * ipBanReq.setIp("192.168.1.100");
 * ipBanReq.setTime(1440); // 封禁24小时（1440分钟）
 * ipBanReq.setPrivileges(Arrays.asList("join_channel"));
 * }</pre>
 * 
 * @author Agora
 * @version 1.0
 * @see BanRuleUpdateReq
 */
@Data
public class BanRuleCreateReq {
    
    /**
     * 项目的App ID（必填）
     * 
     * <p>声网项目的唯一标识符，用于确定封禁规则的作用范围。</p>
     * 
     * <p><b>获取方式：</b></p>
     * <ul>
     *   <li>从Agora Console项目管理页面复制</li>
     *   <li>调用获取所有项目API，读取响应体中vendor_key字段的值</li>
     * </ul>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>必须是有效的32位字符串</li>
     *   <li>封禁规则仅在指定的App ID项目内生效</li>
     * </ul>
     */
    private String appid;
    
    /**
     * 频道名称（可选）
     * 
     * <p>指定要封禁的频道名称。如果设置此字段，封禁规则将仅在该频道内生效。</p>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>对特定频道进行管控</li>
     *   <li>临时封禁用户在某个会议室的权限</li>
     *   <li>与uid配合使用，精确封禁特定用户在特定频道的权限</li>
     * </ul>
     * 
     * <p><b>注意：</b>如果不设置cname，封禁规则将在所有频道中生效。</p>
     */
    private String cname;
    
    /**
     * 用户ID（可选）
     * 
     * <p>指定要封禁的用户ID。如果设置此字段，封禁规则将仅对该用户生效。</p>
     * 
     * <p><b>重要提醒：</b></p>
     * <ul>
     *   <li>不要设置为0，0为无效值</li>
     *   <li>必须是正整数</li>
     *   <li>用户ID由应用程序定义和管理</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>封禁违规用户</li>
     *   <li>临时限制特定用户权限</li>
     *   <li>配合频道名称实现精确控制</li>
     * </ul>
     */
    private Long uid;
    
    /**
     * 用户IP地址（可选）
     * 
     * <p>指定要封禁的IP地址。如果设置此字段，来自该IP的所有用户都将被封禁。</p>
     * 
     * <p><b>IP封禁特点：</b></p>
     * <ul>
     *   <li>影响范围较大，需谨慎使用</li>
     *   <li>适用于防止恶意攻击或垃圾用户</li>
     *   <li>支持IPv4格式，如192.168.1.100</li>
     * </ul>
     * 
     * <p><b>注意：</b>不要设置为"0"或无效IP格式。</p>
     */
    private String ip;
    
    /**
     * 封禁时长（分钟）（可选）
     * 
     * <p>设置封禁的持续时间，以分钟为单位。</p>
     * 
     * <p><b>取值范围：</b>[1, 1440]（1分钟到24小时）</p>
     * 
     * <p><b>自动调整规则：</b></p>
     * <ul>
     *   <li>如果设置值在0和1之间，Agora自动设置为1</li>
     *   <li>如果设置值大于1440，Agora自动设置为1440</li>
     *   <li>如果设置值为0，封禁规则不生效，服务器将符合规则的用户下线，用户可重新登录</li>
     * </ul>
     * 
     * <p><b>注意：</b>time和time_in_seconds只能使用其中一个。如果两个都设置，time_in_seconds优先生效。</p>
     */
    private Integer time;
    
    /**
     * 封禁时长（秒）（可选）
     * 
     * <p>设置封禁的持续时间，以秒为单位，比分钟单位更精确。</p>
     * 
     * <p><b>取值范围：</b>[10, 86430]（10秒到24小时）</p>
     * 
     * <p><b>自动调整规则：</b></p>
     * <ul>
     *   <li>如果设置值在0和10之间，Agora自动设置为10</li>
     *   <li>如果设置值大于86430，Agora自动设置为86430</li>
     *   <li>如果设置值为0，封禁规则不生效</li>
     * </ul>
     * 
     * <p><b>优先级：</b>如果同时设置了time和time_in_seconds，此字段优先生效。</p>
     * <p><b>默认值：</b>如果都不设置，系统默认封禁时长为3600秒（60分钟）。</p>
     */
    private Integer timeInSeconds;
    
    /**
     * 要封禁的用户权限列表（必填）
     * 
     * <p>指定要封禁的用户权限类型。可以选择一个或多个权限进行封禁。</p>
     * 
     * <p><b>支持的权限类型：</b></p>
     * <ul>
     *   <li><code>"join_channel"</code> - 禁止用户加入频道或将用户踢出频道</li>
     *   <li><code>"publish_audio"</code> - 禁止用户发布音频流</li>
     *   <li><code>"publish_video"</code> - 禁止用户发布视频流</li>
     * </ul>
     * 
     * <p><b>权限组合示例：</b></p>
     * <pre>{@code
     * // 禁止发布音频和视频（禁言禁视频）
     * Arrays.asList("publish_audio", "publish_video")
     * 
     * // 完全踢出频道
     * Arrays.asList("join_channel")
     * 
     * // 只禁止发布音频（禁言）
     * Arrays.asList("publish_audio")
     * }</pre>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>至少需要指定一个权限类型</li>
     *   <li>可以传入多个权限实现组合封禁</li>
     *   <li>join_channel权限影响最大，会直接阻止用户加入频道</li>
     * </ul>
     */
    private List<String> privileges;
}