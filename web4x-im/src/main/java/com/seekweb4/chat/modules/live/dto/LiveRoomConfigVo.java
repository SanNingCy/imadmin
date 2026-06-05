package com.seekweb4.chat.modules.live.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 会议室配置返回 VO（用于管理端展示）
 */
@Data
public class LiveRoomConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 规则ID（来自 t_live_billing_rule.id） */
    private String id;

    /** 每分钟消耗（来自 t_live_billing_rule.unit_price） */
    private BigDecimal stepConsumptionToken;

    /** 人数档位选项（来自 t_live_user_tier_config） */
    private List<LiveRoomConfigOptionVo> userTierOptions;

    /** 时长选项（来自 t_live_time_duration_config） */
    private List<LiveRoomConfigOptionVo> timeOline;

    /** 续费/计费规则文案（来自 t_live_billing_rule.rounding_rule） */
    private String renewalRules;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

