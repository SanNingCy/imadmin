package com.seekweb4.chat.agora.service.api;

import com.seekweb4.chat.agora.bean.dto.*;
import com.seekweb4.chat.agora.bean.dto.*;
import com.seekweb4.chat.agora.bean.req.BanRuleCreateReq;
import com.seekweb4.chat.agora.bean.req.BanRuleUpdateReq;
import com.seekweb4.chat.agora.bean.req.CreateKickOutRule;
import com.seekweb4.chat.agora.service.api.response.BanRuleListResponse;
import com.seekweb4.chat.agora.service.api.response.BanRuleResponse;
import com.seekweb4.chat.agora.service.api.response.ChannelListResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import com.seekweb4.chat.agora.service.api.request.BanRuleCreateRequest;
import com.seekweb4.chat.agora.service.api.request.BanRuleUpdateRequest;

/**
 * 声网RTC频道管理API服务接口
 * 
 * <p>基于OpenFeign实现的声网RTC频道管理HTTP客户端，提供以下核心功能：</p>
 * <ul>
 *   <li><b>频道信息查询</b> - 获取频道列表、用户列表、用户状态查询</li>
 *   <li><b>用户权限管理</b> - 创建、查询、更新、删除用户封禁规则</li>
 *   <li><b>踢出规则管理</b> - 支持按频道、用户ID、IP等条件创建踢出规则</li>
 *   <li><b>项目管理</b> - 项目创建、查询、配置和使用量统计</li>
 * </ul>
 * 
 * <p><b>API文档参考：</b></p>
 * <ul>
 *   <li>频道管理：https://docs.agora.io/cn/voice-calling/channel-management-api</li>
 *   <li>用户封禁：https://docs.agora.io/cn/voice-calling/channel-management-api/endpoint/ban-user-privileges</li>
 *   <li>项目管理：https://docs.agora.io/en/voice-calling/channel-management-api/agora-console-rest-api</li>
 * </ul>
 * 
 * <p><b>认证方式：</b></p>
 * <p>所有接口使用HTTP Basic认证，需要传入Base64编码的"客户ID:客户密钥"。</p>
 * 
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 获取频道列表
 * ChannelListResponseDto channels = rtcChannelAPIService.getChannelList(
 *     "your-app-id", 0, 10, basicAuth);
 * 
 * // 创建封禁规则
 * BanRuleCreateReq banReq = new BanRuleCreateReq();
 * banReq.setAppid("your-app-id");
 * banReq.setPrivileges(Arrays.asList("join_channel"));
 * BanRuleResponseDto response = rtcChannelAPIService.createBanRule(banReq, basicAuth);
 * }</pre>
 * 
 * @author Agora
 * @version 1.0
 * @see BanRuleCreateReq
 * @see BanRuleResponseDto
 * @see ChannelListResponseDto
 * @see UserListResponseDto
 */
@Headers("Content-Type:application/json;charset=UTF-8")
public interface RtcChannelAPIService {
    
    /**
     * 创建踢出规则（旧版本接口，兼容保留）
     * 
     * <p>创建用于踢出指定用户的规则。此方法为向后兼容保留，建议使用 {@link #createBanRule} 替代。</p>
     * 
     * @param kickOutRule 踢出规则创建请求
     * @param basicAuth Basic认证字符串，格式：Base64("客户ID:客户密钥")
     * @return 踢出规则创建响应
     *
     */
    @RequestLine("POST /dev/v1/kicking-rule")
    @Headers({"Authorization: {basicAuth}"})
    CreateKickOutRuleDto createKickOutRule(CreateKickOutRule kickOutRule, @Param("basicAuth") String basicAuth);
    
    // ==================== 频道信息查询 ====================
    
    /**
     * 获取频道列表
     * 
     * <p>获取指定项目下所有频道的列表。支持分页查询，便于处理大量频道数据。</p>
     * 
     * <p><b>功能特点：</b></p>
     * <ul>
     *   <li>支持分页查询，避免单次返回数据过多</li>
     *   <li>返回频道名称和用户数量统计</li>
     *   <li>实时数据，反映当前频道状态</li>
     * </ul>
     * 
     * @param appId 项目的App ID，可从Agora Console获取
     * @param pageNo 要查询的页码，从0开始，默认值为0（第一页）
     * @param pageSize 每页显示的频道数量，取值范围[1,500]，默认值为100
     * @param basicAuth Basic认证字符串，格式：Base64("客户ID:客户密钥")
     * @return 频道列表响应，包含频道信息和分页信息
     * 
     * @see ChannelListResponseDto
     * @see ChannelInfoDto
     */
    @RequestLine("GET /dev/v1/channel/{appId}?page_no={pageNo}&page_size={pageSize}")
    @Headers({"Authorization: {basicAuth}"})
    ChannelListResponse getChannelList(@Param("appId") String appId,
                                       @Param("pageNo") Integer pageNo,
                                       @Param("pageSize") Integer pageSize,
                                       @Param("basicAuth") String basicAuth);
    
    /**
     * 获取指定频道的用户列表
     * 
     * <p>获取指定频道中所有用户的列表。返回当前在线用户的UID列表。</p>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>监控频道内用户活动</li>
     *   <li>统计在线用户数量</li>
     *   <li>用户管理和权限控制</li>
     * </ul>
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称，必须是当前存在的频道
     * @param basicAuth Basic认证字符串
     * @return 用户列表响应，包含用户UID列表
     * 
     * @see UserListResponseDto
     * @see UserListDataDto
     */
    @RequestLine("GET /dev/v1/channel/user/{appId}/{channelName}")
    @Headers({"Authorization: {basicAuth}"})
    UserListResponseDto getUserList(@Param("appId") String appId,
                                 @Param("channelName") String channelName,
                                 @Param("basicAuth") String basicAuth);
    
    /**
     * 获取指定频道的主播列表
     * 
     * <p>在直播模式下，仅获取指定频道中的主播（Host）列表。</p>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>仅在直播场景（Live Broadcasting）下有效</li>
     *   <li>只返回角色为主播（Host）的用户</li>
     *   <li>观众（Audience）不会出现在返回列表中</li>
     * </ul>
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param hostsOnly 固定值"hosts"，表示仅返回主播列表
     * @param basicAuth Basic认证字符串
     * @return 主播列表响应
     */
    @RequestLine("GET /dev/v1/channel/user/{appId}/{channelName}/{hostsOnly}")
    @Headers({"Authorization: {basicAuth}"})
    UserListResponseDto getHostList(@Param("appId") String appId, 
                                  @Param("channelName") String channelName, 
                                  @Param("hostsOnly") String hostsOnly, 
                                  @Param("basicAuth") String basicAuth);
    
    /**
     * 查询用户在频道中的状态
     * 
     * <p>查询指定用户是否在指定频道中，以及用户的角色信息。</p>
     * 
     * <p><b>返回信息包括：</b></p>
     * <ul>
     *   <li>用户是否在频道中（in_channel）</li>
     *   <li>用户是否为主播（isHost）</li>
     *   <li>用户角色编号（role）</li>
     * </ul>
     * 
     * @param appId 项目的App ID
     * @param uid 用户ID，字符串格式
     * @param channelName 频道名称
     * @param basicAuth Basic认证字符串
     * @return 用户状态响应
     * 
     * @see UserStatusResponseDto
     * @see UserStatusDataDto
     */
    @RequestLine("GET /dev/v1/channel/user/property/{appId}/{uid}/{channelName}")
    @Headers({"Authorization: {basicAuth}"})
    UserStatusResponseDto getUserStatus(@Param("appId") String appId,
                                     @Param("uid") String uid,
                                     @Param("channelName") String channelName,
                                     @Param("basicAuth") String basicAuth);
    
    // ==================== 用户封禁规则管理 ====================
    
    /**
     * 创建用户封禁规则
     * 
     * <p>创建用于封禁指定用户权限的规则。支持多种封禁条件和权限类型。</p>
     * 
     * <p><b>支持的封禁权限：</b></p>
     * <ul>
     *   <li><code>join_channel</code> - 禁止用户加入频道或将用户踢出频道</li>
     *   <li><code>publish_audio</code> - 禁止用户发布音频</li>
     *   <li><code>publish_video</code> - 禁止用户发布视频</li>
     * </ul>
     * 
     * <p><b>封禁条件：</b></p>
     * <ul>
     *   <li>可基于频道名称（cname）、用户ID（uid）、IP地址进行封禁</li>
     *   <li>支持组合条件，如同时指定频道和用户ID</li>
     *   <li>支持时长设置，分钟或秒为单位</li>
     * </ul>
     * 
     * @param request 封禁规则创建请求，包含封禁条件和权限设置
     * @param basicAuth Basic认证字符串
     * @return 封禁规则创建响应，包含规则ID用于后续管理
     * 
     * @see BanRuleCreateRequest
     * @see BanRuleResponse
     */
    @RequestLine("POST /dev/v1/kicking-rule")
    @Headers({"Authorization: {basicAuth}"})
    BanRuleResponse createBanRule(BanRuleCreateRequest request, @Param("basicAuth") String basicAuth);
    
    /**
     * 获取封禁规则列表
     * 
     * <p>获取指定App ID下的所有封禁规则列表。</p>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>查看当前生效的封禁规则</li>
     *   <li>审计用户权限管理记录</li>
     *   <li>获取规则ID用于更新或删除操作</li>
     * </ul>
     * 
     * @param appId 项目的App ID
     * @param basicAuth Basic认证字符串
     * @return 封禁规则列表响应
     * 
     * @see BanRuleListResponseDto
     * @see BanRuleInfoDto
     */
    @RequestLine("GET /dev/v1/kicking-rule?appid={appId}")
    @Headers({"Authorization: {basicAuth}"})
    BanRuleListResponse getBanRuleList(@Param("appId") String appId, @Param("basicAuth") String basicAuth);
    
    /**
     * 更新封禁规则
     * 
     * <p>更新指定的封禁规则。可以修改封禁条件、权限类型或封禁时长。</p>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>需要提供准确的规则ID</li>
     *   <li>更新后的规则立即生效</li>
     *   <li>建议在更新前先获取规则列表确认规则存在</li>
     * </ul>
     * 
     * @param ruleId 要更新的规则ID，通过创建或查询接口获得
     * @param request 封禁规则更新请求
     * @param basicAuth Basic认证字符串
     * @return 封禁规则更新响应
     * 
     * @see BanRuleUpdateReq
     * @see BanRuleResponseDto
     */
    @RequestLine("PUT /dev/v1/kicking-rule?id={ruleId}")
    @Headers({"Authorization: {basicAuth}"})
    BanRuleResponseDto updateBanRule(@Param("ruleId") Long ruleId,
                                     BanRuleUpdateRequest request,
                                   @Param("basicAuth") String basicAuth);
    
    /**
     * 删除封禁规则
     * 
     * <p>删除指定的封禁规则。删除后，该规则将不再生效。</p>
     * 
     * <p><b>删除效果：</b></p>
     * <ul>
     *   <li>规则立即失效，被封禁的用户可以重新获得权限</li>
     *   <li>规则从系统中永久删除，无法恢复</li>
     *   <li>建议在删除重要规则前进行确认</li>
     * </ul>
     * 
     * @param appId 项目的App ID
     * @param ruleId 要删除的规则ID
     * @param basicAuth Basic认证字符串
     * @return 封禁规则删除响应
     * 
     * @see BanRuleResponseDto
     */
    @RequestLine("DELETE /dev/v1/kicking-rule?appid={appId}&id={ruleId}")
    @Headers({"Authorization: {basicAuth}"})
    BanRuleResponse deleteBanRule(@Param("appId") String appId,
                                   @Param("ruleId") Long ruleId, 
                                   @Param("basicAuth") String basicAuth);
}
