package com.seekweb4.chat.modules.creditScore.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditScoreUserTypeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String userId;
    private String idno;
    private String lianghao;

    private Integer type;
    private Integer subtype;

    /**
     * 当前累计分
     */
    private BigDecimal currentScore;

    /**
     * 用户总信用分（t_member_details.credit_score 或 sum当前累计分）
     */
    private BigDecimal totalCreditScore;

    /**
     * 类型配置中的单次基础分（score）
     */
    private BigDecimal baseScore;

    /**
     * 类型配置中的封顶上限（max_limit）
     */
    private BigDecimal maxLimit;

    /**
     * 类型配置状态
     */
    private Integer status;

    private Integer orderNum;

    /**
     * 类型配置：构成展示（1:展示 0:不展示）
     */
    private Integer constituteShow;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

