package com.seekweb4.chat.modules.creditScore.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreditScoreUserTypePageQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * t_member.id / t_member_credit_score_type.user_id
     */
    private String userId;

    /**
     * 用户 idno（模糊）
     */
    private String idno;

    /**
     * 用户靓号（模糊）
     */
    private String lianghao;

    private Integer type;
    private Integer subtype;

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    /**
     * 可选排序：例如 update_time desc
     */
    private String orderBy;
}

