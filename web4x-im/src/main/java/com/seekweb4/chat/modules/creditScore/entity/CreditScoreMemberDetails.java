package com.seekweb4.chat.modules.creditScore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * t_member_details 用户总信用分
 */
@Data
public class CreditScoreMemberDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Integer isDeleted;

    private String userId;

    private BigDecimal creditScore;

    /**
     * 信用分状态
     */
    private Integer creditStatus;
}

