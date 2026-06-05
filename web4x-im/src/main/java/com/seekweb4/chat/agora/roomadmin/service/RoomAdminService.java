package com.seekweb4.chat.agora.roomadmin.service;


import com.seekweb4.chat.agora.roomadmin.bean.*;

import java.util.List;
import java.util.Map;

/**
 * 会议室后台管理服务接口
 * 
 * <p>提供会议室后台管理相关的业务逻辑处理。</p>
 * <p>包括查询、解散等功能。</p>
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
public interface RoomAdminService {

    /**
     * 获取会议列表
     * 
     * <p>分页查询会议列表，支持多种筛选条件和排序方式。</p>
     * 
     * @param request 查询请求对象
     * @return 分页的会议列表
     * @throws Exception 查询过程中的异常
     */
    RoomListResponse getRoomList(RoomListQueryReq request) throws Exception;

    /**
     * 获取会议聊天室详情
     * 
     * <p>根据会议ID获取聊天室的详细信息。</p>
     * 
     * @param request 查询请求对象
     * @return 聊天室详细信息
     * @throws Exception 查询过程中的异常
     */
    RoomDetailResponse getChatRoomDetail(RoomDetailQueryReq request) throws Exception;

    /**
     * 解散会议
     * 
     * <p>强制解散指定的会议，释放所有相关资源。</p>
     * 
     * @param request 解散请求对象
     * @return 解散结果
     * @throws Exception 解散过程中的异常
     */
    Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception;

    /**
     * 批量解散会议
     * 
     * <p>批量解散多个会议，用于批量管理操作。</p>
     * 
     * @param request 批量解散请求对象
     * @return 批量解散结果
     * @throws Exception 批量解散过程中的异常
     */
    Map<String, Object> batchDestroyRooms(Map<String, Object> request) throws Exception;

    /**
     * 获取会议室在线用户列表
     *
     * @param roomId 会议室ID
     * @return 在线用户列表
     * @throws Exception 查询异常
     */
    List<Map<String, Object>> getRoomUsers(String roomId) throws Exception;

    /**
     * 获取会议统计数据
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param groupId 群ID（可选）
     * @return 统计数据
     * @throws Exception 查询异常
     */
    Map<String, Object> getRoomStats(Long startTime, Long endTime, String groupId) throws Exception;
}
