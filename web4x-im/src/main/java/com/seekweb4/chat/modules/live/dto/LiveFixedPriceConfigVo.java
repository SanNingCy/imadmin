package com.seekweb4.chat.modules.live.dto;

import com.seekweb4.chat.modules.live.entity.LiveFixedPriceConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 固定价格分页/详情：连表带出时长、人数档位展示字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LiveFixedPriceConfigVo extends LiveFixedPriceConfig {

    private static final long serialVersionUID = 1L;

    private String durationName;

    /** 时长值(分钟) */
    private Integer durationValue;

    private String tierName;

    /** 人数上限 */
    private Integer tierValue;
}
