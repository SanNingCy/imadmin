package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

/**
 * 简化版会议室创建请求
 * 
 * <p>该请求类用于简化会议室创建流程，前端只需要传递群ID和群主ID，</p>
 * <p>其他配置（如appId、sceneId、roomId、payload等）都由后端自动生成。</p>
 * 
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>快速创建会议室</li>
 *   <li>群聊内创建会议</li>
 *   <li>简化前端调用流程</li>
 * </ul>
 * 
 * <p><strong>自动生成的配置：</strong></p>
 * <ul>
 *   <li>appId - 从配置文件读取</li>
 *   <li>sceneId - 根据会议类型自动生成</li>
 *   <li>roomId - 基于群ID和时间戳生成唯一ID</li>
 *   <li>payload - 包含会议基本信息和配置</li>
 *   <li>聊天室配置 - 使用默认配置</li>
 *   <li>IM配置 - 从配置文件读取</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class SimpleRoomCreateReq {
    
    /**
     * 群ID，关联的群聊ID
     * 
     * <p>用于标识会议室所属的群聊，支持群内会议管理。</p>
     * <p>如果为空，则创建独立的会议室。</p>
     */
    @NotBlank(message = "群ID不能为空")
    private String groupId;
    
    /**
     * 群主ID，会议室创建者
     * 
     * <p>会议室的创建者和默认管理员，拥有会议室的管理权限。</p>
     * <p>包括：踢出用户、管理会议室设置、查看统计信息等。</p>
     */
    @NotBlank(message = "群主ID不能为空")
    private String ownerId;
    
    /**
     * 会议室类型（可选）
     * 
     * <p>默认为0（直播场景），支持以下类型：</p>
     * <ul>
     *   <li>0 - 直播场景（live_streaming）</li>
     *   <li>1 - 语音聊天（voice_chat）</li>
     *   <li>2 - 视频通话（video_call）</li>
     *   <li>3 - 会议场景（conference）</li>
     * </ul>
     */
    private Integer type = 0;
    
    /**
     * 会议室名称（可选）
     * 
     * <p>如果不提供，将自动生成会议室名称。</p>
     * <p>格式：群会议_时间戳 或 会议室_时间戳</p>
     */
    private String roomName;
    
    /**
     * 最大用户数（可选）
     * 
     * <p>会议室最大容纳用户数，未传时后端默认使用500000。</p>
     * <p>超过限制的用户将无法加入会议室。</p>
     */
    private Integer maxUsers;

    /**
     * 全员开麦（可选）。未传时后端默认使用 true
     */
    private Boolean allMic;

    /**
     * 全员禁言（可选）。未传时后端默认使用 false
     */
    private Boolean allMute;
}
