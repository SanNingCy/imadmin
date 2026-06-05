package com.seekweb4.chat.modules.creditScore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * t_member_credit_score_type 用户累计信用分（按 type/subtype）
 */
@Data
public class MemberCreditScoreType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Integer isDeleted;

    private Integer type;

    private Integer subtype;

    /**
     * 已累计获取分数
     */
    private BigDecimal currentScore;

    /**
     * 用户id（与 t_member.id / t_credit_score_log.user_id 对应）
     */
    private String userId;
}

