package com.seekweb4.chat.agora.service.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 频道信息
 * 
 * @author liangbo
 */
@Data
public class ChannelInfo {
    
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