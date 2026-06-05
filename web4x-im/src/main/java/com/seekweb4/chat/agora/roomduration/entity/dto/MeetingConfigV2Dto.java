package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.List;

@Data
@Accessors(chain = true)
public class MeetingConfigV2Dto {
    private String id;
    private Boolean allMic;
    private Boolean allMute;
    private java.math.BigDecimal stepConsumptionToken;
    private String timeZone;
    private List<Map<String, Object>> userTierOptions;
    private List<Map<String, Object>> timeOline;
    // 续费规则
    private String renewalRules;
    // 是否免密支付
    private Boolean isNonPayPwd;
    private String createTime; // yyyy-MM-dd HH:mm:ss
    private String updateTime; // yyyy-MM-dd HH:mm:ss
}


