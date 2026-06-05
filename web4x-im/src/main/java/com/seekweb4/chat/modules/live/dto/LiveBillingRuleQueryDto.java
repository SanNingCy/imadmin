package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LiveBillingRuleQueryDto extends LiveAdminPageQueryDto {
    private Long id;
    private BigDecimal unitPrice;
    private Integer status;
    private String createBy;
    private String updateBy;
}

