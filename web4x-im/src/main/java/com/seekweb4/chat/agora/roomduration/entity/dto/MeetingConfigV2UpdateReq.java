package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.DecimalMin;
import java.util.Map;
import java.util.List;

@Data
@Accessors(chain = true)
public class MeetingConfigV2UpdateReq {
    private String id;
    private Boolean allMic;
    private Boolean allMute;
    @DecimalMin(value = "0", message = "每分钟消耗token不能为负")
    private java.math.BigDecimal stepConsumptionToken;
    private String timeZone;
    private List<Map<String, Object>> userTierOptions;
    private List<Map<String, Object>> timeOline;

    private Boolean isNonPayPwd; // 是否免密支付

    // 续费规则
    private String renewalRules;
    // 删除的选项列表
    private List<Map<String, Object>> deleteUserTierOptions;
    private List<Map<String, Object>> deleteTimeOline;
}


