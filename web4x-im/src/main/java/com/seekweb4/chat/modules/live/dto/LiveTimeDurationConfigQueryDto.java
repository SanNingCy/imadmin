package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

@Data
public class LiveTimeDurationConfigQueryDto extends LiveAdminPageQueryDto {
    private Long id;
    private String durationName;
    private Integer durationValue;
    private Integer status;
}

