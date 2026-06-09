package com.seekweb4.chat.modules.live.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会议室固定价格配置 t_live_fixed_price_config
 */
@Data
public class LiveFixedPriceConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long durationId;

    private Long tierId;

    private BigDecimal fixedPrice;

    /** 定价模式：ODIC / USDT */
    private String pricingMode;

    /** 状态 0:禁用 1:启用 */
    private Integer status;

    private String remark;

    private String createBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer isDeleted;
}
