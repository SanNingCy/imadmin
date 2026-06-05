package com.seekweb4.chat.agora.roomduration.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.List;

@Data
@Accessors(chain = true)
@Document(collection = "meeting_config_v2")
public class MeetingConfigV2Entity {
    @Id
    private String id;

    // 是否全禁麦
    private Boolean allMic;

    // 是否全禁言
    private Boolean allMute;

    // 每分钟消耗的token（支持小数）
    private java.math.BigDecimal stepConsumptionToken;

    // 时区，如UTC+8
    private String timeZone;

    // 续费规则
    private String renewalRules;

    // 人数档位选项（数组项格式：{"name":"100人以下","value":"100"}）
    private List<Map<String, Object>> userTierOptions;

    // 时长选项（数组项格式：{"label":"30分钟","value":30}）
    private List<Map<String, Object>> timeOline;

    private Boolean isNonPayPwd; // 是否免密支付

    private Long createTime;
    private Long updateTime;
}


