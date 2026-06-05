package com.seekweb4.chat.agora.service.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户列表数据
 * 
 * @author liangbo
 */
@Data
public class UserListData {
    
    /**
     * 指定频道是否存在
     * true: 频道存在
     * false: 频道不存在
     * 注意：当channel_exist为false时，不返回其他所有字段
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
     * 频道中所有用户的用户ID
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