package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LiveFixedPriceConfigQueryDto extends LiveAdminPageQueryDto {

    private Long id;

    private Long durationId;

    private Long tierId;

    private Integer status;

    private BigDecimal fixedPrice;

    private String createBy;

    private String updateBy;
}
