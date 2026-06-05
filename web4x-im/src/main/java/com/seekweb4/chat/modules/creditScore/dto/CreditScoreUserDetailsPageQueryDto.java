package com.seekweb4.chat.modules.creditScore.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreditScoreUserDetailsPageQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * t_member.id / t_member_details.user_id
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

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    /**
     * 可选排序：例如 d.update_time desc
     */
    private String orderBy;

    /**
     * 可选：信用分类型(type)筛选
     */
    private Integer type;

    /**
     * 可选：信用分子类型(subtype)筛选
     */
    private Integer subtype;
}

