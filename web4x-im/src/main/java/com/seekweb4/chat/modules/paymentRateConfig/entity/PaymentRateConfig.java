package com.seekweb4.chat.modules.paymentRateConfig.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seekweb4.chat.core.persistence.DataEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 支付入金费率配置表
 *
 * @author system
 * @since 2025-10-24
 */
@Data
public class PaymentRateConfig   implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 类型(1:入金wx 2:提现 3：IM内部)
     */
    private Integer paymentType;

    /**
     * 费率
     */
    private BigDecimal rate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
//    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
//    private LocalDateTime updateTime;

    /**
     * 创建人id
     */
    private String createBy;

    /**
     * 更新人id
     */
    private String updateBy;

    /**
     * 删除标记(0:否 1:是)
     */
    private Integer isDeleted;
}