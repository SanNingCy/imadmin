package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.dto.v2.*;
import com.seekweb4.chat.agora.bean.req.v2.*;
import com.seekweb4.chat.agora.bean.dto.v2.*;
import com.seekweb4.chat.agora.bean.req.v2.*;


/**
 * 房间V2服务接口
 * 提供房间管理的相关操作，包括创建、更新、销毁、查询等功能
 */
public interface IRoomV2Service {
    
    /**
     * 创建房间
     * 
     * @param roomCreateReq 房间创建请求参数
     * @return 房间创建结果
     * @throws Exception 创建过程中的异常
     */
    RoomCreateDto create(RoomCreateReq roomCreateReq) throws Exception;

    /**
     * 简化版创建会议室
     * 
     * <p>前端只需要传递群ID和群主ID，其他配置由后端自动生成。</p>
     * <p>包括：appId、sceneId、roomId、payload、聊天室配置、IM配置等。</p>
     * 
     * @param simpleRoomCreateReq 简化版会议室创建请求参数
     * @return 会议室创建结果，包含所有自动生成的配置信息
     * @throws Exception 创建过程中的异常
     */
    SimpleRoomCreateDto createSimpleRoom(SimpleRoomCreateReq simpleRoomCreateReq) throws Exception;

    /**
     * 添加房间到列表
     * 
     * @param addRoomReq 添加房间请求参数
     * @throws Exception 添加过程中的异常
     */
    void addRoomList(AddRoomReq addRoomReq) throws Exception;

    /**
     * 更新房间信息
     * 
     * @param roomUpdateReq 房间更新请求参数
     * @throws Exception 更新过程中的异常
     */
    void update(RoomUpdateReq roomUpdateReq) throws Exception;

    /**
     * 销毁房间
     * 
     * @param roomDestroyReq 房间销毁请求参数
     * @throws Exception 销毁过程中的异常
     */
    void destroy(RoomDestroyReq roomDestroyReq) throws Exception;

    /**
     * 获取房间列表
     * 
     * @param roomListReq 房间列表查询请求参数
     * @return 房间列表查询结果
     */
    RoomListDto<RoomListEntity> getRoomList(RoomListReq roomListReq);

    /**
     * 查询房间详情
     * 
     * @param roomQueryReq 房间查询请求参数
     * @return 房间查询结果
     * @throws Exception 查询过程中的异常
     */
    RoomQueryDto query(RoomQueryReq roomQueryReq) throws Exception;

    /**
     * 从列表中移除房间
     * 
     * @param roomDestroyReq 房间销毁请求参数
     * @throws Exception 移除过程中的异常
     */
    void removeRoomList(RoomDestroyReq roomDestroyReq) throws Exception;

    /**
     * 更新房间状态
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @param status 新状态 (active/inactive/destroyed)
     * @throws Exception 更新过程中的异常
     */
    void updateRoomStatus(String appId, String sceneId, String roomId, String status) throws Exception;

    /**
     * 用户加入房间时更新状态为活跃
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @throws Exception 更新过程中的异常
     */
    void userJoinRoom(String appId, String sceneId, String roomId) throws Exception;

    /**
     * 用户离开房间时更新状态
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @throws Exception 更新过程中的异常
     */
    void userLeaveRoom(String appId, String sceneId, String roomId) throws Exception;

    /**
     * 获取房间当前状态
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @return 房间当前状态
     * @throws Exception 查询过程中的异常
     */
    String getRoomStatus(String appId, String sceneId, String roomId) throws Exception;

    /**
     * 查询群内正在进行的会议
     * 
     * @param appId 应用ID
     * @param groupId 群ID
     * @param sceneId 场景ID（可选）
     * @return 群会议信息
     * @throws Exception 查询过程中的异常
     */
    GroupMeetingResponseDto getGroupActiveMeetings(String appId, String groupId, String sceneId) throws Exception;
    
    /**
     * 根据会议室ID查询会议室详情
     * 
     * @param roomId 会议室ID
     * @return 会议室详情
     * @throws Exception 查询过程中的异常
     */
    RoomDetailDto getRoomDetail(String roomId) throws Exception;
    
    /**
     * 更新会议室设置
     * 
     * @param request 更新请求
     * @return 是否更新成功
     * @throws Exception 更新过程中的异常
     */
    boolean updateRoomSettings(RoomSettingsUpdateReq request) throws Exception;
}
