package com.seekweb4.chat.modules.withdrawConfig.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提币配置表，对应表 withdraw_config
 */
@Data
public class WithdrawConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 最低提现金额
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BigDecimal minAmount;

    /**
     * 最⾼提现金额
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BigDecimal maxAmount;

    /**
     * 状态 1启用 0关闭
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

