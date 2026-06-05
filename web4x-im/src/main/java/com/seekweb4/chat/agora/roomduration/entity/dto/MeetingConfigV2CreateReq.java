package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.List;

@Data
@Accessors(chain = true)
public class MeetingConfigV2CreateReq {
    @NotNull(message = "是否全禁麦不能为空")
    private Boolean allMic;
    @NotNull(message = "是否全禁言不能为空")
    private Boolean allMute;
    @NotNull(message = "每分钟消耗token不能为空")
    @DecimalMin(value = "0", message = "每分钟消耗token不能为负")
    private java.math.BigDecimal stepConsumptionToken;
    @NotBlank(message = "时区不能为空")
    private String timeZone;
    private List<Map<String, Object>> userTierOptions;
    private List<Map<String, Object>> timeOline;
}


