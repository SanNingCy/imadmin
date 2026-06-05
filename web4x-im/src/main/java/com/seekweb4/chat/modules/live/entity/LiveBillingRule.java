package com.seekweb4.chat.modules.live.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会议计费规则表 t_live_billing_rule
 */
@Data
public class LiveBillingRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 规则（可存 JSON 或枚举文本） */
    private String renewalRules;
    private String roundingRule;

    /** 状态 0:禁用 1:启用 */
    private Integer status;

    private String remark;

    private String createBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 是否删除 0:未删除 1:已删除 */
    private Integer isDeleted;
}

