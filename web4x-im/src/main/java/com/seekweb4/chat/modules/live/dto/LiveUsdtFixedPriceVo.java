package com.seekweb4.chat.modules.live.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * USDT 定价展示（数据存 t_live_fixed_price_config，成本/利润实时计算）
 */
@Data
public class LiveUsdtFixedPriceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long durationId;

    private Long tierId;

    private String durationName;

    /** 会议时长(分钟)，来自时长配置 */
    private Integer durationMinutes;

    /** 会议人数，来自人数档位配置 */
    private Integer peopleCount;

    private BigDecimal costPriceUsdt;

    /** 会议售价(USDT)，对应 fixed_price */
    private BigDecimal salePriceUsdt;

    private BigDecimal profitUsdt;

    private Integer status;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
