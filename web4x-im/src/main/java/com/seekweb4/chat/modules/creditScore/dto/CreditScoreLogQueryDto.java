package com.seekweb4.chat.modules.creditScore.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreditScoreLogQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** t_member.id / t_credit_score_log.user_id */
    private String userId;

    /** 与 userId 二选一或组合：按会员 idno 模糊搜 */
    private String idno;

    /** 与 userId 二选一或组合：按靓号模糊搜 */
    private String lianghao;

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Integer type;
    private Integer subtype;

    private String orderBy;
}
