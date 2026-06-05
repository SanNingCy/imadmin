package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 会议室配置分页查询 DTO（计费规则 + 人数/时长选项）
 */
@Data
public class LiveRoomConfigQueryDto extends LiveAdminPageQueryDto {
    private Long id;
    private BigDecimal unitPrice;
    private Integer status;
    private String createBy;
    private String updateBy;
}

