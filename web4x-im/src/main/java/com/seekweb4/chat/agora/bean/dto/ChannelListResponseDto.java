package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

/**
 * 频道列表响应DTO
 * 
 * <p>声网RTC频道列表查询API的响应数据传输对象。</p>
 * <p>用于封装频道列表查询接口返回的响应数据，包含请求状态和频道列表详细信息。</p>
 * 
 * <p><b>主要用途：</b></p>
 * <ul>
 *   <li>频道监控 - 获取项目下所有活跃频道的列表</li>
 *   <li>数据统计 - 统计频道数量和用户分布情况</li>
 *   <li>运营管理 - 监控频道使用情况和用户活跃度</li>
 * </ul>
 * 
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * ChannelListResponseDto response = rtcChannelAPIService.getChannelList(
 *     "your-app-id", 0, 10, basicAuth);
 * 
 * if (Boolean.TRUE.equals(response.getSuccess())) {
 *     ChannelListDataDto data = response.getData();
 *     System.out.println("总频道数: " + data.getTotalSize());
 *     
 *     for (ChannelInfoDto channel : data.getChannels()) {
 *         System.out.println("频道: " + channel.getChannelName() + 
 *                           ", 用户数: " + channel.getUserCount());
 *     }
 * }
 * }</pre>
 * 
 * @author Agora
 * @version 1.0
 * @see ChannelListDataDto
 * @see ChannelInfoDto
 */
@Data
public class ChannelListResponseDto {
    
    /**
     * 请求执行状态
     * 
     * <p>表示频道列表查询请求是否成功执行。</p>
     * <ul>
     *   <li>true - 请求成功，数据有效</li>
     *   <li>false - 请求失败或数据无效</li>
     * </ul>
     */
    private Boolean success;
    
    /**
     * 频道列表数据
     * 
     * <p>包含频道总数、分页信息和具体的频道详情列表。</p>
     * <p>当success为true时，此字段包含有效的频道数据。</p>
     * 
     * @see ChannelListDataDto
     */
    private ChannelListDataDto data;
}