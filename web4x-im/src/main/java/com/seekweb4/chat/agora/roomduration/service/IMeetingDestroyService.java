package com.seekweb4.chat.agora.roomduration.service;

import com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq;

import java.util.Map;

/**
 * 会议室销毁服务接口
 * 提供移动端会议室销毁功能
 */
public interface IMeetingDestroyService {
    
    /**
     * 销毁会议室
     * 
     * @param request 销毁请求参数（包含appId, sceneId, roomId）
     * @return 销毁结果
     * @throws Exception 销毁过程中的异常
     */
    Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception;
}
