package com.seekweb4.chat.agora.controller.v2;

import com.seekweb4.chat.agora.bean.dto.KickOutRuleDto;
import com.seekweb4.chat.agora.bean.dto.R;
import com.seekweb4.chat.agora.bean.req.UserKickOutReq;
import com.seekweb4.chat.agora.service.IAgoraUserService;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器 V2版本
 * 
 * <p>该控制器提供用户相关的管理功能，主要用于房间或房间内的用户权限控制。</p>
 * <p>支持对违规用户进行踢出处理，维护良好的用户环境和服务秩序。</p>
 * <p>新增房间管理员管理功能，支持管理员权限的分配和管理。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>用户踢出 - 将违规用户移出房间或房间</li>
 *   <li>踢出规则管理 - 设置和管理踢出规则</li>
 *   <li>用户权限控制 - 基于规则进行权限管理</li>
 *   <li>房间管理员管理 - 添加、移除、查询房间管理员</li>
 * </ul>
 * 
 * <p><strong>API版本：</strong>v2</p>
 * <p><strong>路径前缀：</strong>/v2/users</p>
 * <p><strong>响应格式：</strong>JSON</p>
 * 
 * <p><strong>应用场景：</strong></p>
 * <ul>
 *   <li>直播间管理 - 踢出发送不当内容的用户</li>
 *   <li>会议室控制 - 移除干扰会议的参与者</li>
 *   <li>教育平台 - 管理课堂纪律</li>
 *   <li>游戏房间 - 踢出作弊或恶意用户</li>
 *   <li>社交应用 - 维护社区环境</li>
 * </ul>
 * 
 * <p><strong>权限说明：</strong></p>
 * <ul>
 *   <li>只有房间管理员或应用管理员可以执行踢出操作</li>
 *   <li>踢出操作会立即生效，被踢用户无法继续参与</li>
 *   <li>可以设置踢出时长，支持临时和永久踢出</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 2.0
 * @since 2.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgoraUserController {
    
    /**
     * 用户服务接口，处理用户相关的业务逻辑
     */
    @Resource
    private IAgoraUserService iAgoraUserService;

    @Resource
    private MemberService memberService;

    /** 应用ID，从配置文件读取 */
    @Value("${whitelist.token.appId}")
    private String appId;

    /**
     * 踢出用户
     * 
     * <p>根据指定的踢出规则将用户从房间或房间中移除。</p>
     * <p>支持设置踢出时长、踢出原因等详细参数。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/users/kickOut</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>踢出效果：</strong></p>
     * <ul>
     *   <li>立即断开用户连接</li>
     *   <li>阻止用户重新加入（在踢出时长内）</li>
     *   <li>清理用户在房间内的状态信息</li>
     *   <li>记录踢出操作日志</li>
     * </ul>
     * 
     * <p><strong>踢出类型：</strong></p>
     * <ul>
     *   <li><strong>临时踢出：</strong>设置具体的踢出时长，到期后自动解除</li>
     *   <li><strong>永久踢出：</strong>用户将被永久禁止加入该房间</li>
     *   <li><strong>全局踢出：</strong>用户将被禁止使用整个应用</li>
     * </ul>
     * 
     * <p><strong>请求参数说明：</strong></p>
     * <ul>
     *   <li><strong>userId：</strong>要踢出的用户ID</li>
     *   <li><strong>channelName：</strong>房间或房间名称</li>
     *   <li><strong>duration：</strong>踢出时长（秒），0表示永久</li>
     *   <li><strong>reason：</strong>踢出原因描述</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "userId": "user123",
     *   "channelName": "live_room_001", 
     *   "duration": 3600,
     *   "reason": "违反社区规定",
     *   "operator": "admin001"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "ruleId": "rule_789",
     *     "userId": "user123",
     *     "channelName": "live_room_001",
     *     "duration": 3600,
     *     "effectiveTime": 1694073000000,
     *     "expireTime": 1694076600000,
     *     "reason": "违反社区规定",
     *     "status": "active"
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * <p><strong>注意事项：</strong></p>
     * <ul>
     *   <li>踢出操作需要适当的权限验证</li>
     *   <li>建议记录详细的操作日志用于审计</li>
     *   <li>可以考虑添加申诉机制</li>
     *   <li>批量踢出时需要注意性能影响</li>
     * </ul>
     * 
     * @param req 用户踢出请求对象，包含踢出用户所需的参数
     * @return 统一响应对象，成功时包含踢出规则的详细信息
     * @throws Exception 当踢出过程中出现异常时抛出，如用户不存在、权限不足等
     */
    @PostMapping("/kickOut")
    @ResponseBody
    public R<KickOutRuleDto> kickOut(@Validated @RequestBody UserKickOutReq req) throws Exception {
        log.info("kick out req:{}", req);
        KickOutRuleDto kickOutRuleDto = iAgoraUserService.kickOut(req);
        return R.success(kickOutRuleDto);
    }

    // ==================== 房间管理员管理接口 ====================

    /**
     * 添加房间管理员
     * 
     * <p>为指定房间添加管理员，管理员将获得房间管理权限。</p>
     * <p>管理员ID通过用户ID唯一标识，确保每个用户在一个房间中只能有一个管理员身份。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/admins/{userId}</p>
     * 
     * <p><strong>管理员权限包括：</strong></p>
     * <ul>
     *   <li>踢出房间内用户</li>
     *   <li>禁言和解禁用户</li>
     *   <li>管理房间设置</li>
     *   <li>查看房间统计信息</li>
     * </ul>
     *
     * @param roomId 房间名称
     * @param userId 要设置为管理员的用户ID
     * @param operator 操作者ID（可选，用于记录操作日志）
     * @return 操作结果
     */
    @PostMapping("/channels/{roomId}/admins/{userId}")
    public AjaxJson addChannelAdmin(@PathVariable String roomId,
                                     @PathVariable String userId,
                                     @RequestParam(required = false) String operator) {
        try {
            log.info("添加房间管理员 - appId: {}, channelName: {}, userId: {}, operator: {}", 
                    appId, roomId, userId, operator);
            
            boolean result = iAgoraUserService.addRoomIdAdmin(appId, roomId, userId, operator);
            if (result) {
                log.info("房间管理员添加成功 - userId: {}, roomId: {}", userId, roomId);
                return AjaxJson.success();
            } else {
                return AjaxJson.fail("管理员添加失败，可能已经是管理员");
            }
        } catch (Exception e) {
            log.error("添加房间管理员失败", e);
            return AjaxJson.fail("添加房间管理员失败");
        }
    }

    /**
     * 取消房间管理员
     * 
     * <p>移除指定用户的房间管理员身份，撤销其管理权限。</p>
     * <p>被撤销的用户将立即失去房间管理权限。</p>
     * 
     * <p><strong>HTTP方法：</strong>DELETE</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/admins/{userId}</p>
     *
     * @param roomId 房间名称
     * @param userId 要取消管理员身份的用户ID
     * @param operator 操作者ID（可选，用于记录操作日志）
     * @return 操作结果
     */
    @DeleteMapping("/channels/{roomId}/admins/{userId}")
    public AjaxJson removeChannelAdmin(@PathVariable String roomId,
                                        @PathVariable String userId,
                                        @RequestParam(required = false) String operator) {
        try {
            log.info("取消房间管理员 - appId: {}, roomId: {}, userId: {}, operator: {}",
                    appId, roomId, userId, operator);
            
            boolean result = iAgoraUserService.removeRoomIdAdmin(appId, roomId, userId, operator);
            if (result) {
                log.info("房间管理员取消成功 - userId: {}, roomId: {}", userId, roomId);
                return AjaxJson.success();
            } else {
                return AjaxJson.fail("管理员取消失败，用户可能不是管理员");
            }
        } catch (Exception e) {
            log.error("取消房间管理员失败", e);
            return AjaxJson.fail("取消房间管理员失败");
        }
    }

    /**
     * 获取房间管理员列表
     * 
     * <p>查询指定房间的所有管理员列表。</p>
     * <p>返回管理员的详细信息，包括用户ID、设置时间等。</p>
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/admins</p>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 0,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "userId": "admin001",
     *       "channelName": "test_channel",
     *       "appId": "app123",
     *       "createTime": 1694073000000,
     *       "operator": "super_admin"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param roomId 房间名称
     * @return 管理员列表
     */
    @GetMapping("/channels/{roomId}/admins")
    public AjaxJson getChannelAdmins(@PathVariable String roomId) {
        try {
            log.info("获取房间管理员列表 - appId: {}, roomId: {}", appId, roomId);
            List<Map<String, Object>> admins = iAgoraUserService.getRoomIdAdmins(appId, roomId);
            log.info("房间管理员列表查询成功 - roomId: {}, 管理员数量: {}", roomId, admins.size());
            return AjaxJson.success().setDataList(admins);
        } catch (Exception e) {
            log.error("获取房间管理员列表失败", e);
            return AjaxJson.fail("获取房间管理员列表失败");
        }
    }

    /**
     * 检查用户是否为房间管理员
     * 
     * <p>验证指定用户是否具有指定房间的管理员权限。</p>
     * <p>主要用于权限验证和前端界面控制。</p>
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/admins/{userId}/check</p>
     *
     * @param roomId 房间名称
     * @param userId 要检查的用户ID
     * @return 检查结果，true表示是管理员，false表示不是
     */
    @GetMapping("/channels/{roomId}/admins/{userId}/check")
    public AjaxJson isChannelAdmin(@PathVariable String roomId, @PathVariable String userId) {
        try {
            log.info("检查用户管理员身份 - appId: {}, roomId: {}, userId: {}", appId, roomId, userId);
            boolean isAdmin = iAgoraUserService.isRoomIdAdmin(appId, roomId, userId);
            log.debug("管理员身份检查结果 - userId: {}, isAdmin: {}", userId, isAdmin);
            Map<String, Object> result = new HashMap<>();
            result.put("isAdmin", isAdmin);
            return AjaxJson.success().setData(result);
        } catch (Exception e) {
            log.error("检查用户管理员身份失败", e);
            return AjaxJson.fail("检查用户管理员身份失败");
        }
    }

    /**
     * 批量添加房间管理员
     * 
     * <p>一次性为房间添加多个管理员，提高批量操作效率。</p>
     * <p>如果某个用户已经是管理员，会跳过该用户继续处理其他用户。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/admins/batch</p>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "userIds": ["user001", "user002", "user003"],
     *   "operator": "super_admin"
     * }
     * </pre>
     *
     * @param roomId 房间名称
     * @param userIds 要添加为管理员的用户ID列表
     * @param operator 操作者ID（可选）
     * @return 操作结果，包含成功和失败的统计信息
     */
    @PostMapping("/channels/{roomId}/admins/batch")
    public AjaxJson batchAddChannelAdmins(@PathVariable String roomId,
                                          @RequestBody List<String> userIds, @RequestParam(required = false) String operator) {
        try {
            log.info("批量添加房间管理员 - appId: {}, channelName: {}, userIds: {}, operator: {}", 
                    appId, roomId, userIds, operator);

            Map<String, Object> result = iAgoraUserService.batchAddRoomIdAdmins(appId, roomId, userIds, operator);
            log.info("批量添加房间管理员完成 - channelName: {}", roomId);
            return AjaxJson.success().setData(result);
        } catch (Exception e) {
            log.error("批量添加房间管理员失败", e);
            return AjaxJson.fail("批量添加房间管理员失败: " + e.getMessage());
        }
    }

    // ==================== 踢出用户管理接口 ====================

    /**
     * 添加踢出用户记录
     * 
     * <p>将用户踢出记录存储在payload中，记录用户ID和用户名。</p>
     * <p>每个被踢出的用户都会包含详细的踢出信息，如踢出时间、操作者、原因等。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/kicked/{userId}</p>
     * 
     * <p><strong>踢出记录包含：</strong></p>
     * <ul>
     *   <li>用户ID - 被踢出用户的唯一标识</li>
     *   <li>用户名 - 被踢出用户的显示名称</li>
     *   <li>踢出时间 - 记录踢出操作的时间戳</li>
     *   <li>操作者 - 执行踢出操作的用户</li>
     *   <li>踢出原因 - 踢出的具体原因说明</li>
     * </ul>
     * 
     * @param roomId 房间名称
     * @param userId 被踢出的用户ID
     * @param userName 被踢出的用户名
     * @param operator 操作者ID（可选，用于记录操作日志）
     * @param reason 踢出原因（可选）
     * @return 操作结果
     */
    @PostMapping("/channels/{roomId}/kicked/{userId}")
    public AjaxJson addKickedUser(@PathVariable String roomId, @PathVariable String userId,
                                   @RequestParam(required = false) String userName, @RequestParam(required = false) String operator,
                                   @RequestParam(required = false) String reason) {
        try {
            // 如果userName为空，可以从数据库查询或使用默认值
            if (StringUtils.isBlank(userName)) {
                // 根据userId查询用户名
                Member member = memberService.get(userId);
                userName = member != null ? member.getNickname() : "未知用户";
            }

            log.info("添加踢出用户记录 - appId: {}, roomId: {}, userId: {}, userName: {}, operator: {}, reason: {}", 
                    appId, roomId, userId, userName, operator, reason);
            
            boolean result = iAgoraUserService.addKickedUser(appId, roomId, userId, userName, operator, reason);
            log.info("踢出用户记录添加成功 - userId: {}, userName: {}, roomId: {}", userId, userName, roomId);
            return AjaxJson.success().put("isKicked", result);
        } catch (Exception e) {
            log.error("添加踢出用户记录失败", e);
            return AjaxJson.fail("添加踢出用户记录失败");
        }
    }

    /**
     * 移除踢出用户记录
     * 
     * <p>从payload中移除用户的踢出记录，解除用户的踢出状态。</p>
     * <p>移除后用户将可以重新加入房间。</p>
     * 
     * <p><strong>HTTP方法：</strong>DELETE</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/kicked/{userId}</p>
     *
     * @param roomId 房间名称
     * @param userId 要移除踢出记录的用户ID
     * @param operator 操作者ID（可选，用于记录操作日志）
     * @return 操作结果
     */
    @DeleteMapping("/channels/{roomId}/kicked/{userId}")
    public AjaxJson removeKickedUser(@PathVariable String roomId,
                                      @PathVariable String userId, @RequestParam(required = false) String operator) {
        try {
            log.info("移除踢出用户记录 - appId: {}, roomId: {}, userId: {}, operator: {}", 
                    appId, roomId, userId, operator);
            boolean result = iAgoraUserService.removeKickedUser(appId, roomId, userId, operator);
            if (result) {
                log.info("踢出用户记录移除成功 - userId: {}, roomId: {}", userId, roomId);
                return AjaxJson.success().put("isRemoveKicked", true);
            } else {
                return AjaxJson.fail("踢出用户记录移除失败，用户不在踢出列表中");
            }
        } catch (Exception e) {
            log.error("移除踢出用户记录失败", e);
            return  AjaxJson.fail("移除踢出用户记录失败");
        }
    }

    /**
     * 获取踢出用户列表
     * 
     * <p>查询指定房间的所有踢出用户列表。</p>
     * <p>返回踢出用户的详细信息，包括用户ID、用户名、踢出时间等。</p>
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/kicked</p>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 0,
     *   "message": "Success",
     *   "data": [
     *     {
     *       "userId": "user123",
     *       "userName": "违规用户",
     *       "kickTime": 1694073000000,
     *       "operator": "admin001",
     *       "reason": "发布不当内容"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param roomId 房间名称
     * @return 踢出用户列表
     */
    @GetMapping("/channels/{roomId}/kicked")
    public AjaxJson getKickedUsers(@PathVariable String roomId) {
        try {
            log.info("获取踢出用户列表 - appId: {}, roomId: {}", appId, roomId);
            List<Map<String, Object>> kickedUsers = iAgoraUserService.getKickedUsers(appId, roomId);
            log.info("踢出用户列表查询成功 - roomId: {}, 踢出用户数量: {}", roomId, kickedUsers.size());
            return AjaxJson.success().setDataList(kickedUsers);
        } catch (Exception e) {
            log.error("获取踢出用户列表失败", e);
            return AjaxJson.fail("获取踢出用户列表失败");
        }
    }

    /**
     * 检查用户是否被踢出
     * 
     * <p>验证指定用户是否在踢出用户列表中。</p>
     * <p>主要用于权限验证和用户状态检查。</p>
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/kicked/{userId}/check</p>
     *
     * @param roomId 房间名称
     * @param userId 要检查的用户ID
     * @return 检查结果，true表示用户被踢出，false表示用户未被踢出
     */
    @GetMapping("/channels/{roomId}/kicked/{userId}/check")
    public AjaxJson isUserKicked(@PathVariable String roomId,
                                  @PathVariable String userId) {
        try {
            log.info("检查用户是否被踢出 - appId: {}, roomId: {}, userId: {}", appId, roomId, userId);
            
            boolean isKicked = iAgoraUserService.isUserKicked(appId, roomId, userId);
            log.debug("用户踢出状态检查结果 - userId: {}, isKicked: {}", userId, isKicked);
            return AjaxJson.success().put("isKicked", isKicked);
        } catch (Exception e) {
            log.error("检查用户踢出状态失败", e);
            return AjaxJson.fail("检查用户踢出状态失败");
        }
    }

    /**
     * 批量添加踢出用户记录
     * 
     * <p>一次性踢出多个用户，提高批量操作效率。</p>
     * <p>如果某个用户已经在踢出列表中，会跳过该用户继续处理其他用户。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/users/channels/{roomId}/kicked/batch</p>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * [
     *   {
     *     "userId": "user001",
     *     "userName": "违规用户1"
     *   },
     *   {
     *     "userId": "user002", 
     *     "userName": "违规用户2"
     *   }
     * ]
     * </pre>
     *
     * @param roomId 房间名称
     * @param kickedUsers 要踢出的用户信息列表，包含userId和userName
     * @param operator 操作者ID（可选）
     * @param reason 踢出原因（可选）
     * @return 操作结果，包含成功和失败的统计信息
     */
    @PostMapping("/channels/{roomId}/kicked/batch")
    public AjaxJson batchAddKickedUsers(@PathVariable String roomId,
                                        @RequestBody List<Map<String, String>> kickedUsers,
                                        @RequestParam(required = false) String operator,
                                        @RequestParam(required = false) String reason) {
        try {
            log.info("批量添加踢出用户记录 - appId: {}, roomId: {}, kickedUsers: {}, operator: {}, reason: {}", 
                    appId, roomId, kickedUsers, operator, reason);

            Map<String, Object> result = iAgoraUserService.batchAddKickedUsers(appId, roomId, kickedUsers, operator, reason);
            log.info("批量添加踢出用户记录完成 - roomId: {}", roomId);
            return AjaxJson.success().setData(result);
        } catch (Exception e) {
            log.error("批量添加踢出用户记录失败", e);
            return AjaxJson.fail("批量添加踢出用户记录失败");
        }
    }
}
