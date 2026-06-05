package com.seekweb4.chat.agora.roomduration.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MeetingConfigV2PageResp {
    private long count;
    @JsonIgnore
    private long total;
    @JsonIgnore
    private int pageNum;
    @JsonIgnore
    private int pageSize;
    private List<MeetingConfigV2Dto> list;
}


