package com.seekweb4.chat.agora.bean.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 频道信息DTO
 * 
 * @author Agora
 */
@Data
public class ChannelInfoDto {
    
    /**
     * 频道名称
     */
    @JsonProperty("channel_name")
    private String channelName;
    
    /**
     * 频道中的用户总数
     */
    @JsonProperty("user_count")
    private Integer userCount;
}