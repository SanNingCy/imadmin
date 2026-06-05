package com.seekweb4.chat.agora.bean.dto.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 简化版会议室创建响应
 * 
 * <p>该响应类包含会议室创建后的完整信息，包括自动生成的配置和前端需要的所有数据。</p>
 * <p>前端可以直接使用这些信息进行后续的会议室操作。</p>
 * 
 * <p><strong>包含信息：</strong></p>
 * <ul>
 *   <li>会议室基本信息（ID、名称、状态等）</li>
 *   <li>自动生成的配置信息</li>
 *   <li>聊天室配置信息</li>
 *   <li>IM配置信息</li>
 *   <li>时间戳信息</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class SimpleRoomCreateDto {
    
    /**
     * 应用ID
     * 从配置文件自动读取
     */
    private String appId;
    
    /**
     * 场景ID
     * 根据会议室类型自动生成
     */
    private String sceneId;
    
    /**
     * 会议室ID
     * 基于群ID和时间戳生成的唯一标识
     */
    private String roomId;
    
    /**
     * 群ID
     * 会议室所属的群聊ID
     */
    private String groupId;
    
    /**
     * 群主ID
     * 会议室创建者和管理员
     */
    private String ownerId;
    
    /**
     * 会议室状态
     * - pending_create : 待创建
     * - active: 房间活跃状态，有用户在线
     * - inactive: 房间非活跃状态，无用户在线
     * - destroyed: 房间已销毁
     */
    private String status;
    
    /**
     * 会议室负载数据
     * 包含会议室的详细配置信息
     */
    private Map<String, Object> payload;
    
    /**
     * 创建时间（时间戳，毫秒）
     */
    private Long createTime;
    
    /**
     * 更新时间（时间戳，毫秒）
     */
    private Long updateTime;
    
    /**
     * 聊天室配置信息
     * 用于创建对应的聊天室
     */
    private ChatRoomConfigDto chatRoomConfig;
    
    /**
     * IM配置信息
     * 用于即时消息服务
     */
    private ImConfigDto imConfig;
    
    /**
     * 聊天室配置DTO
     */
    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Accessors(chain = true)
    public static class ChatRoomConfigDto {
        /**
         * 聊天室最大用户数
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
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
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
