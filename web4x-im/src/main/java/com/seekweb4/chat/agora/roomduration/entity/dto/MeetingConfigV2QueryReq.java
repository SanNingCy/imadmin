package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MeetingConfigV2QueryReq {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}


