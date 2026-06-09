package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LiveUsdtMeetingPriceBatchSaveReq {

    /** 已有会议室时长配置 id（与 durationMinutes 二选一） */
    private Long durationId;

    /** 会议时长(分钟)，不存在时自动写入时长配置表 */
    private Integer durationMinutes;

    private Integer status;

    private String remark;

    private String createBy;

    private List<TierItem> tiers;

    @Data
    public static class TierItem {

        /** 已有人数档位 id（与 peopleCount 二选一） */
        private Long tierId;

        /** 会议人数，不存在时自动写入人数配置表 */
        private Integer peopleCount;

        private BigDecimal salePriceUsdt;
    }
}
