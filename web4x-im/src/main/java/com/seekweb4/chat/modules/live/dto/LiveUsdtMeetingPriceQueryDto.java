package com.seekweb4.chat.modules.live.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LiveUsdtMeetingPriceQueryDto extends LiveAdminPageQueryDto {

    private Long id;

    /** 会议室时长配置 id */
    private Long durationId;

    /** 人数档位配置 id */
    private Long tierId;

    private Integer status;
}
