package com.seekweb4.chat.agora.service.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 频道列表数据
 * 
 * @author liangbo
 */
@Data
public class ChannelListData {
    
    /**
     * 频道列表
     * 包含多个频道信息对象的数组
     * 如果指定的页面不包含任何频道，此字段为空
     */
    private List<ChannelInfo> channels;
    
    /**
     * 指定项目下的频道总数
     */
    @JsonProperty("total_size")
    private Integer totalSize;
}