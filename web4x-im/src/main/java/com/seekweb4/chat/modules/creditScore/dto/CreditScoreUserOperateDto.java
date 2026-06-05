package com.seekweb4.chat.modules.creditScore.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreditScoreUserOperateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * t_member.id / t_credit_score_log.user_id
     */
    private String userId;

    /**
     * 信用分类型（自定义接口传；系统接口可忽略）
     */
    private Integer type;

    /**
     * 子类型（可选；不传时按 0 处理）
     */
    private Integer subtype;

    /**
     * 描述（用于写 t_credit_score_log.desc）
     */
    private String desc;

    /**
     * 手动输入分数（不传则按类型配置 score；type=5 平台贡献亦取类型配置 score 为单次分值）
     */
    private BigDecimal score;
}

