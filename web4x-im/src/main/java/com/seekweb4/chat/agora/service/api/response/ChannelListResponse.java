package com.seekweb4.chat.agora.service.api.response;

import lombok.Data;

/**
 * 频道列表响应
 * 
 * @author liangbo
 */
@Data
public class ChannelListResponse {
    
    /**
     * 请求状态
     * true: 成功
     * false: 保留供将来使用
     */
    private Boolean success;
    
    /**
     * 频道统计信息
     */
    private ChannelListData data;
}