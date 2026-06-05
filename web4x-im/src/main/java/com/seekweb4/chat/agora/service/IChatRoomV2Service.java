package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomCreateDto;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomCreateReq;

/**
 * 聊天室V2服务接口
 * 提供聊天室V2版本的相关操作
 */
public interface IChatRoomV2Service {
    
    /**
     * 创建聊天室
     * 
     * @param req 聊天室创建请求参数
     * @return 聊天室创建结果
     * @throws Exception 创建过程中的异常
     */
    ChatRoomCreateDto Create(ChatRoomCreateReq req) throws Exception;
}
