package com.seekweb4.chat.agora.controller.v2;

import com.seekweb4.chat.agora.bean.dto.R;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomCreateDto;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomCreateReq;
import com.seekweb4.chat.agora.service.IChatRoomV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 聊天室管理控制器 V2版本
 * 
 * <p>该控制器提供聊天室相关的REST API接口，包括聊天室的创建、管理等功能。</p>
 * <p>基于Agora Chat SDK实现，支持大规模实时聊天场景。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>创建聊天室</li>
 *   <li>管理聊天室成员</li>
 *   <li>聊天室配置管理</li>
 * </ul>
 * 
 * <p><strong>API版本：</strong>v2</p>
 * <p><strong>路径前缀：</strong>/v2/chatRoom</p>
 * <p><strong>响应格式：</strong>JSON</p>
 * 
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>直播间聊天功能</li>
 *   <li>在线教育课堂讨论</li>
 *   <li>社交应用群聊功能</li>
 *   <li>游戏内聊天系统</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 2.0
 * @since 2.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v2/chatRoom", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomV2Controller {

    /**
     * 聊天室服务接口，提供聊天室相关的业务逻辑处理
     */
    @Resource
    private IChatRoomV2Service chatRoomV2Service;

    /**
     * 创建聊天室
     * 
     * <p>根据提供的参数创建一个新的聊天室，返回创建结果和聊天室相关信息。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/chatRoom/create</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>功能特性：</strong></p>
     * <ul>
     *   <li>支持自定义聊天室名称和描述</li>
     *   <li>可设置聊天室最大成员数量</li>
     *   <li>支持聊天室权限配置</li>
     *   <li>自动生成聊天室唯一ID</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "name": "我的聊天室",
     *   "description": "聊天室描述",
     *   "maxUsers": 500,
     *   "owner": "user123"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "chatRoomId": "chatroom_12345",
     *     "name": "我的聊天室",
     *     "description": "聊天室描述",
     *     "maxUsers": 500,
     *     "owner": "user123",
     *     "created": "2025-09-07T10:30:00Z"
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * @param req 聊天室创建请求对象，包含创建聊天室所需的参数
     * @return 统一响应对象，成功时包含创建的聊天室信息
     * @throws Exception 当创建过程中出现异常时抛出，如网络异常、权限不足等
     */
    @PostMapping("/create")
    @ResponseBody
    public R<ChatRoomCreateDto> create(@Validated @RequestBody ChatRoomCreateReq req) throws Exception {
        ChatRoomCreateDto createDto = chatRoomV2Service.Create(req);
        return R.success(createDto);
    }
}
