package com.seekweb4.chat.agora.bean.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户列表数据DTO
 * 
 * @author Agora
 */
@Data
public class UserListDataDto {
    
    /**
     * 频道名称
     */
    private String channelName;
    
    /**
     * 用户ID列表（兼容字符串格式）
     */
    private List<String> uids;
    
    /**
     * 指定频道是否存在
     * true: 频道存在
     * false: 频道不存在
     */
    @JsonProperty("channel_exist")
    private Boolean channelExist;
    
    /**
     * 频道模式
     * 1: 通信模式（COMMUNICATION）
     * 2: 直播模式（LIVE_BROADCASTING）
     */
    private Integer mode;
    
    /**
     * 频道中用户的总数
     * 仅当mode为1时返回此字段
     */
    private Integer total;
    
    /**
     * 频道中所有用户的用户ID（原始Long格式）
     * 仅当mode为1时返回此字段
     */
    private List<Long> users;
    
    /**
     * 频道中所有主播的用户ID
     * 仅当mode为2时返回此字段
     */
    private List<Long> broadcasters;
    
    /**
     * 频道中前10,000个观众的用户ID
     * 仅当mode为2且未填入hosts_only参数时返回此字段
     */
    private List<Long> audience;
    
    /**
     * 频道中观众的总数
     * 仅当mode为2且未填入hosts_only参数时返回此字段
     */
    @JsonProperty("audience_total")
    private Integer audienceTotal;
}