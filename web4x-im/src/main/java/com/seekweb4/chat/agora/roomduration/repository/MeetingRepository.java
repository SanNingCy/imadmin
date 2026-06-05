package com.seekweb4.chat.agora.roomduration.repository;

import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends MongoRepository<MeetingEntity, String> {
    long countByOwnerIdAndStartTimeGreaterThanEqual(String ownerId, Long startTime);
    long countByOwnerIdAndStartTimeLessThanAndEndTimeGreaterThan(String ownerId, Long endExclusive, Long startInclusive);
    MeetingEntity findTopByGroupIdOrderByStartTimeDesc(String groupId);
    // 检查用户是否有未销毁的会议（不基于时间，只基于状态）
    long countByOwnerIdAndStatusNot(String ownerId, String status);
    // 查询群内所有未结束的会议
    List<MeetingEntity> findByGroupIdAndStartTimeLessThanAndEndTimeGreaterThan(String groupId, Long endExclusive, Long startInclusive);
    // 查询群内活跃状态的会议（基于status字段）
    List<MeetingEntity> findByGroupIdAndStatus(String groupId, String status);
    // 查询群内非销毁状态的会议
    List<MeetingEntity> findByGroupIdAndStatusNot(String groupId, String status);
    // 查询指定时间范围内的会议（用于统计）
    List<MeetingEntity> findByCreateTimeBetweenOrderByCreateTimeAsc(Long startTime, Long endTime);
    
    // 查询正在进行中的会议数量（active + inactive状态）
    long countByStatusIn(List<String> statuses);
    
    // 查询指定群组正在进行中的会议数量
    long countByGroupIdAndStatusIn(String groupId, List<String> statuses);
    
    // 根据状态查询会议数量
    long countByStatus(String status);
    
    // 根据创建者查询会议数量
    long countByOwnerId(String ownerId);
    
    // 根据群组查询会议数量
    long countByGroupId(String groupId);
    
    // 根据状态和创建者查询会议数量
    long countByStatusAndOwnerId(String status, String ownerId);
    
    // 根据创建时间范围查询会议数量
    long countByCreateTimeBetween(Long startTime, Long endTime);
    
    // 根据状态和创建时间范围查询会议数量
    long countByStatusAndCreateTimeBetween(String status, Long startTime, Long endTime);
    
    // 根据roomId查找会议
    Optional<MeetingEntity> findByRoomId(String roomId);
    // 根据roomId和状态查找会议
    Optional<MeetingEntity> findByRoomIdAndStatusNot(String roomId, String status);

    // 查找群组内最新的非销毁会议
    MeetingEntity findTopByGroupIdAndStatusNotOrderByStartTimeDesc(String groupId, String status);
    
    // 查找群组内最新的有效会议（非销毁且未结束）
    MeetingEntity findTopByGroupIdAndStatusNotAndEndTimeGreaterThanOrderByStartTimeDesc(String groupId, String status, Long currentTime);
}


