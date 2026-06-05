package com.seekweb4.chat.agora.bean.enums;

import lombok.Getter;

/**
 * RTC频道规则枚举类
 * 
 * <p>该枚举定义了RTC（实时音视频通信）频道中用户的权限规则类型。</p>
 * <p>用于控制用户在频道内的具体操作权限，实现细粒度的权限管理。</p>
 * 
 * <p><strong>权限控制说明：</strong></p>
 * <ul>
 *   <li>频道权限采用白名单机制，只有明确授权的操作才被允许</li>
 *   <li>权限可以动态调整，实时生效</li>
 *   <li>不同权限可以组合使用，实现复杂的权限控制策略</li>
 * </ul>
 * 
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li><strong>在线教育：</strong>控制学生是否可以发言、开摄像头</li>
 *   <li><strong>会议系统：</strong>管理参会者的音视频权限</li>
 *   <li><strong>直播平台：</strong>控制观众上麦权限</li>
 *   <li><strong>客服系统：</strong>灵活控制通话权限</li>
 * </ul>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * {@code
 * // 检查用户是否有加入频道权限
 * String joinRule = RtcChannelRulesEnum.JOIN_CHANNEL.getRule();
 * 
 * // 设置用户音频发布权限
 * List<String> userPermissions = Arrays.asList(
 *     RtcChannelRulesEnum.JOIN_CHANNEL.getRule(),
 *     RtcChannelRulesEnum.PUBLISH_AUDIO.getRule()
 * );
 * 
 * // 为主持人分配全部权限
 * List<String> hostPermissions = Arrays.stream(RtcChannelRulesEnum.values())
 *     .map(RtcChannelRulesEnum::getRule)
 *     .collect(Collectors.toList());
 * }
 * </pre>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum RtcChannelRulesEnum {
    
    /**
     * 加入频道权限
     * 
     * <p>控制用户是否可以加入RTC频道的基础权限。</p>
     * <p>这是最基本的权限，用户必须拥有此权限才能进入频道。</p>
     * 
     * <p><strong>应用场景：</strong></p>
     * <ul>
     *   <li>会议室准入控制 - 只有被邀请的用户才能加入</li>
     *   <li>付费内容访问 - 需要付费后才能进入直播间</li>
     *   <li>身份验证 - 需要完成实名认证才能参与</li>
     *   <li>黑名单机制 - 被禁用户无法加入任何频道</li>
     * </ul>
     * 
     * <p><strong>权限控制效果：</strong></p>
     * <ul>
     *   <li>有权限：用户可以成功加入频道，开始音视频通信</li>
     *   <li>无权限：用户加入频道失败，收到权限不足的错误提示</li>
     * </ul>
     */
    JOIN_CHANNEL("join_channel"),
    
    /**
     * 音频发布权限
     * 
     * <p>控制用户是否可以在频道中发布（发送）音频流。</p>
     * <p>拥有此权限的用户可以开启麦克风，其他用户能听到其声音。</p>
     * 
     * <p><strong>应用场景：</strong></p>
     * <ul>
     *   <li>在线课堂 - 学生需要举手申请才能发言</li>
     *   <li>大型会议 - 只有演讲者可以开启麦克风</li>
     *   <li>直播连麦 - 主播邀请观众上麦互动</li>
     *   <li>客服通话 - 根据业务流程控制通话时机</li>
     * </ul>
     * 
     * <p><strong>权限控制效果：</strong></p>
     * <ul>
     *   <li>有权限：用户可以开启麦克风，发布音频流给其他用户</li>
     *   <li>无权限：用户的麦克风被禁用，无法发出声音</li>
     * </ul>
     * 
     * <p><strong>技术说明：</strong></p>
     * <ul>
     *   <li>该权限与硬件麦克风状态无关，即使麦克风正常也无法发布音频</li>
     *   <li>权限变更会立即生效，无需用户重新加入频道</li>
     * </ul>
     */
    PUBLISH_AUDIO("publish_audio"),
    
    /**
     * 视频发布权限
     * 
     * <p>控制用户是否可以在频道中发布（发送）视频流。</p>
     * <p>拥有此权限的用户可以开启摄像头，其他用户能看到其视频画面。</p>
     * 
     * <p><strong>应用场景：</strong></p>
     * <ul>
     *   <li>视频会议 - 控制参会者开启摄像头的时机</li>
     *   <li>在线面试 - 面试官和候选人轮流展示</li>
     *   <li>直播带货 - 控制嘉宾出镜权限</li>
     *   <li>远程教学 - 老师控制学生摄像头权限</li>
     * </ul>
     * 
     * <p><strong>权限控制效果：</strong></p>
     * <ul>
     *   <li>有权限：用户可以开启摄像头，发布视频流给其他用户</li>
     *   <li>无权限：用户的摄像头被禁用，其他人看不到其画面</li>
     * </ul>
     * 
     * <p><strong>性能考虑：</strong></p>
     * <ul>
     *   <li>视频流比音频流占用更多带宽和计算资源</li>
     *   <li>在网络条件较差时，可以限制视频权限以保证音频质量</li>
     *   <li>大型会议中限制同时开启视频的人数，避免性能问题</li>
     * </ul>
     * 
     * <p><strong>技术说明：</strong></p>
     * <ul>
     *   <li>该权限与硬件摄像头状态无关，即使摄像头正常也无法发布视频</li>
     *   <li>支持多种视频分辨率和帧率控制</li>
     *   <li>可以与屏幕共享权限结合使用</li>
     * </ul>
     */
    PUBLISH_VIDEO("publish_video");
    
    /**
     * 规则标识符
     * <p>用于在系统中唯一标识该权限规则的字符串。</p>
     */
    private final String rule;

    /**
     * 构造函数
     * 
     * @param rule 规则标识符字符串
     */
    RtcChannelRulesEnum(String rule) {
        this.rule = rule;
    }
}
