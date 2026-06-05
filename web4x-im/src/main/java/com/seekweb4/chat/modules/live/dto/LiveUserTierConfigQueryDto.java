package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

@Data
public class LiveUserTierConfigQueryDto extends LiveAdminPageQueryDto {
    private Long id;
    private String tierName;
    private Integer tierValue;
    private Integer status;
}

