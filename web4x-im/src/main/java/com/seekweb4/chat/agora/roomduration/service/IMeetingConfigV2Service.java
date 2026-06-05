package com.seekweb4.chat.agora.roomduration.service;

import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;

public interface IMeetingConfigV2Service {
    MeetingConfigV2Entity getOrInit();
    MeetingConfigV2Entity save(MeetingConfigV2Entity entity);
}


