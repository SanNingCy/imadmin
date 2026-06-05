package com.seekweb4.chat.modules.live.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会议人数档位配置表 t_live_user_tier_config
 */
@Data
public class LiveUserTierConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String tierName;

    /** 人数上限 */
    private Integer tierValue;

    private Integer tierSort;

    /** 状态 0:禁用 1:启用 */
    private Integer status;

    private String createBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String remark;

    private Integer isDeleted;
}

