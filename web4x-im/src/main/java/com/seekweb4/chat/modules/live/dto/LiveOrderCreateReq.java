package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
public class LiveOrderCreateReq {

    @NotBlank
    private String userId;

    @NotNull
    private Long durationId;

    @NotNull
    private Long tierId;

    @NotBlank
    private String groupIdNo;

    @NotNull
    private Date beginTime;

    @NotNull
    private Date endTime;

    @NotBlank
    private String channelName;

    /** 可选：渠道id */
    private String channelId;

    /** 可选：备注 */
    private String remark;

    /** 可选：创建人 */
    private String createBy;
}

