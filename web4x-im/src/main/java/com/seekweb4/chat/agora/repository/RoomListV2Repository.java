package com.seekweb4.chat.agora.repository;

import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomListV2Repository extends MongoRepository<RoomListV2Entity, String> {
    Page<RoomListV2Entity> findByCreateTimeLessThanAndAppIdAndSceneId(Long createTime, String appId, String sceneId, Pageable pageAble);

    List<RoomListV2Entity> findByCreateTimeLessThan(Long createTime);

    // 使用继承的save方法进行更新操作
    
    /**
     * 根据应用ID、群ID和状态查询房间列表
     */
    List<RoomListV2Entity> findByAppIdAndGroupIdAndStatus(String appId, String groupId, String status);

    /**
     * 根据应用ID、群ID、场景ID和状态查询房间列表
     */
    List<RoomListV2Entity> findByAppIdAndGroupIdAndSceneIdAndStatus(String appId, String groupId, String sceneId, String status);

    /**
     * 根据群ID和状态查询房间列表（不限制应用ID）
     */
    List<RoomListV2Entity> findByGroupIdAndStatus(String groupId, String status);

    /**
     * 根据群ID、场景ID和状态查询房间列表（不限制应用ID）
     */
    List<RoomListV2Entity> findByGroupIdAndSceneIdAndStatus(String groupId, String sceneId, String status);
    
    /**
     * 根据应用ID、群ID查询房间列表（不限制状态）
     */
    List<RoomListV2Entity> findByAppIdAndGroupId(String appId, String groupId);
    
    /**
     * 根据应用ID、群ID、场景ID查询房间列表（不限制状态）
     */
    List<RoomListV2Entity> findByAppIdAndGroupIdAndSceneId(String appId, String groupId, String sceneId);
    
    /**
     * 根据群ID查询房间列表（不限制应用ID和状态）
     */
    List<RoomListV2Entity> findByGroupId(String groupId);
    
    /**
     * 根据群ID、场景ID查询房间列表（不限制应用ID和状态）
     */
    List<RoomListV2Entity> findByGroupIdAndSceneId(String groupId, String sceneId);
    
    /**
     * 根据会议室ID查询房间
     */
    java.util.Optional<RoomListV2Entity> findByRoomId(String roomId);
    /**
     * 根据会议室ID查询房间
     */
    java.util.Optional<RoomListV2Entity> findByRoomIdAndStatusNot(String roomId, String status);

    /**
     * 根据会议室ID查询房间（按更新时间倒序）
     * 当历史数据导致存在重复roomId时，取最新一条使用
     */
    List<RoomListV2Entity> findByRoomIdOrderByUpdateTimeDesc(String roomId);
    
    /**
     * 根据状态查询房间列表
     */
    List<RoomListV2Entity> findByStatus(String status);
    
    /**
     * 根据所有者ID和状态查询房间列表
     */
    List<RoomListV2Entity> findByOwnerIdAndStatusNot(String ownerId, String status);
    
    /**
     * 根据群ID和状态查询房间列表（排除指定状态）
     */
    List<RoomListV2Entity> findByGroupIdAndStatusNot(String groupId, String status);
    
    /**
     * 根据状态列表查询房间列表
     */
    List<RoomListV2Entity> findByStatusIn(List<String> statuses);
    
    /**
     * 根据群ID和状态列表查询房间列表
     */
    List<RoomListV2Entity> findByGroupIdAndStatusIn(String groupId, List<String> statuses);
}
