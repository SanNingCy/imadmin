package com.seekweb4.chat.modules.live.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会议时长配置表 t_live_time_duration_config
 */
@Data
public class LiveTimeDurationConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String durationName;

    /** 时长值(单位:分钟) */
    private Integer durationValue;

    private Integer durationSort;

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

