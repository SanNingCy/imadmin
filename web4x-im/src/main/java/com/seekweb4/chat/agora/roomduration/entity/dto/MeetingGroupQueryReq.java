package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MeetingGroupQueryReq {
    private String groupId;
}
