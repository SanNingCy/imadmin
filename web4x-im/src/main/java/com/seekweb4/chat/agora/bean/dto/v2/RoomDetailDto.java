package com.seekweb4.chat.agora.bean.dto.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 会议室详情响应DTO
 * 
 * @author liangbo
 * @since 2024-01-01
 */
@Data
@Accessors(chain = true)
public class RoomDetailDto {
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 场景ID
     */
    private String sceneId;
    
    /**
     * 会议室ID
     */
    private String roomId;
    
    /**
     * 群组ID
     */
    private String groupId;
    
    /**
     * 群主ID
     */
    private String ownerId;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 负载数据
     */
    private Map<String, Object> payload;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 更新时间
     */
    private Long updateTime;
    
    /**
     * 最后活跃时间
     */
    private Long lastActiveTime;
    
    /**
     * 聊天室配置
     */
    private ChatRoomConfigDto chatRoomConfig;
    
    /**
     * IM配置
     */
    private ImConfigDto imConfig;
    
    /**
     * 聊天室ID
     */
    private String chatRoomId;
    
    /**
     * 聊天室配置DTO
     */
    @Data
    @Accessors(chain = true)
    public static class ChatRoomConfigDto {
        /**
         * 最大用户数
         */
        private Integer maxUsers;
        
        /**
         * 聊天室名称
         */
        private String name;
    }
    
    /**
     * IM配置DTO
     */
    @Data
    @Accessors(chain = true)
    public static class ImConfigDto {
        /**
         * 应用名称
         */
        private String appName;
        
        /**
         * 客户端ID
         */
        private String clientId;
        
        /**
         * 客户端密钥
         */
        private String clientSecret;
        
        /**
         * 组织名称
         */
        private String orgName;
    }
}
