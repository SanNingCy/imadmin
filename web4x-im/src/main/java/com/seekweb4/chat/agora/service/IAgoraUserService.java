package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.dto.KickOutRuleDto;
import com.seekweb4.chat.agora.bean.req.UserKickOutReq;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * 提供用户相关的业务操作，包括用户管理、权限控制和频道管理员管理
 */
public interface IAgoraUserService {
    
    /**
     * 踢出用户
     * 
     * <p>将指定用户从频道中踢出，并创建相应的封禁规则。</p>
     * 
     * @param req 用户踢出请求参数，包含用户信息、频道信息、封禁时长等
     * @return 踢出规则创建结果
     * @throws Exception 踢出操作过程中的异常
     */
    KickOutRuleDto kickOut(UserKickOutReq req) throws Exception;
    
    // ==================== 频道管理员管理接口 ====================
    
    /**
     * 添加频道管理员
     * 
     * <p>为指定频道添加管理员，管理员信息存储在RoomListV2Entity的payload字段中。</p>
     * <p>每个用户在一个频道中只能有一个管理员身份，通过用户ID唯一标识。</p>
     * 
     * @param appId 应用ID
     * @param roomId 频道名称
     * @param userId 要设置为管理员的用户ID
     * @param operator 操作者ID，用于记录操作日志
     * @return true表示添加成功，false表示用户已经是管理员
     * @throws Exception 添加过程中的异常
     */
    boolean addRoomIdAdmin(String appId, String roomId, String userId, String operator) throws Exception;
    
    /**
     * 移除频道管理员
     * 
     * <p>移除指定用户的频道管理员身份，从RoomListV2Entity的payload中删除管理员记录。</p>
     * 
     * @param appId 应用ID
     * @param roomId 频道名称
     * @param userId 要移除管理员身份的用户ID
     * @param operator 操作者ID，用于记录操作日志
     * @return true表示移除成功，false表示用户不是管理员
     * @throws Exception 移除过程中的异常
     */
    boolean removeRoomIdAdmin(String appId, String roomId, String userId, String operator) throws Exception;
    
    /**
     * 获取频道管理员列表
     * 
     * <p>查询指定频道的所有管理员列表，从RoomListV2Entity的payload字段中获取。</p>
     * 
     * @param appId 应用ID
     * @param roomId 频道名称
     * @return 管理员列表，包含管理员详细信息
     * @throws Exception 查询过程中的异常
     */
    List<Map<String, Object>> getRoomIdAdmins(String appId, String roomId) throws Exception;
    
    /**
     * 检查用户是否为频道管理员
     * 
     * <p>验证指定用户是否具有指定频道的管理员权限。</p>
     * 
     * @param appId 应用ID
     * @param roomId 频道名称
     * @param userId 要检查的用户ID
     * @return true表示是管理员，false表示不是管理员
     * @throws Exception 检查过程中的异常
     */
    boolean isRoomIdAdmin(String appId, String roomId, String userId) throws Exception;
    
    /**
     * 批量添加频道管理员
     * 
     * <p>一次性为频道添加多个管理员，提高批量操作效率。</p>
     * <p>如果某个用户已经是管理员，会跳过该用户继续处理其他用户。</p>
     * 
     * @param appId 应用ID
     * @param roomId 频道名称
     * @param userIds 要添加为管理员的用户ID列表
     * @param operator 操作者ID
     * @return 操作结果统计信息，包含成功和失败的数量
     * @throws Exception 批量操作过程中的异常
     */
    Map<String, Object> batchAddRoomIdAdmins(String appId, String roomId, List<String> userIds, String operator) throws Exception;
    
    // ==================== 踢出用户管理接口 ====================
    
    /**
     * 添加踢出用户记录
     * 
     * <p>将用户踢出记录存储在RoomListV2Entity的payload字段中。</p>
     * <p>记录包含用户ID、用户名、踢出时间、操作者等信息。</p>
     * 
     * @param appId 应用ID
     * @param roomId 房间名称
     * @param userId 被踢出的用户ID
     * @param userName 被踢出的用户名
     * @param operator 操作者ID，用于记录操作日志
     * @param reason 踢出原因
     * @return true表示添加成功，false表示用户已在踢出列表中
     * @throws Exception 添加过程中的异常
     */
    boolean addKickedUser(String appId, String roomId, String userId, String userName, String operator, String reason) throws Exception;
    
    /**
     * 移除踢出用户记录
     * 
     * <p>从RoomListV2Entity的payload字段中移除用户踢出记录。</p>
     * <p>通常用于解除用户的踢出状态，允许用户重新加入房间。</p>
     * 
     * @param appId 应用ID
     * @param roomId 房间名称
     * @param userId 要移除踢出记录的用户ID
     * @param operator 操作者ID，用于记录操作日志
     * @return true表示移除成功，false表示用户不在踢出列表中
     * @throws Exception 移除过程中的异常
     */
    boolean removeKickedUser(String appId, String roomId, String userId, String operator) throws Exception;
    
    /**
     * 获取踢出用户列表
     * 
     * <p>查询指定房间的所有踢出用户列表，从RoomListV2Entity的payload字段中获取。</p>
     * 
     * @param appId 应用ID
     * @param roomId 房间名称
     * @return 踢出用户列表，包含用户详细信息
     * @throws Exception 查询过程中的异常
     */
    List<Map<String, Object>> getKickedUsers(String appId, String roomId) throws Exception;
    
    /**
     * 检查用户是否被踢出
     * 
     * <p>验证指定用户是否在踢出用户列表中。</p>
     * 
     * @param appId 应用ID
     * @param roomId 房间名称
     * @param userId 要检查的用户ID
     * @return true表示用户被踢出，false表示用户未被踢出
     * @throws Exception 检查过程中的异常
     */
    boolean isUserKicked(String appId, String roomId, String userId) throws Exception;
    
    /**
     * 批量添加踢出用户记录
     * 
     * <p>一次性踢出多个用户，提高批量操作效率。</p>
     * <p>如果某个用户已经在踢出列表中，会跳过该用户继续处理其他用户。</p>
     * 
     * @param appId 应用ID
     * @param roomId 房间名称
     * @param kickedUsers 要踢出的用户信息列表，包含userId和userName
     * @param operator 操作者ID
     * @param reason 踢出原因
     * @return 操作结果统计信息，包含成功和失败的数量
     * @throws Exception 批量操作过程中的异常
     */
    Map<String, Object> batchAddKickedUsers(String appId, String roomId, List<Map<String, String>> kickedUsers, String operator, String reason) throws Exception;
}
