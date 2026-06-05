package com.seekweb4.chat.agora.roomduration.service;

import com.seekweb4.chat.agora.roomadmin.bean.RoomDestroyReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.*;

import java.util.Map;

public interface IMeetingConfigV2AdminService {
    MeetingConfigV2PageResp pageQuery(MeetingConfigV2QueryReq req);
    MeetingConfigV2Dto getById(String id);
    String create(MeetingConfigV2CreateReq req);
    boolean update(MeetingConfigV2UpdateReq req);
    boolean deleteById(String id);

    Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception;

    boolean insert(MeetingConfigV2UpdateReq req);

    boolean reqBackspace(MeetingConfigV2UpdateReq req);
}


