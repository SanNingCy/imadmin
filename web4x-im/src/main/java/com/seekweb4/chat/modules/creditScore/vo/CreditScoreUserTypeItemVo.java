package com.seekweb4.chat.modules.creditScore.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditScoreUserTypeItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String userId;

    private Integer type;
    private Integer subtype;

    private BigDecimal currentScore;

    private BigDecimal baseScore;
    private BigDecimal maxLimit;
    private Integer status;
    private Integer orderNum;

    /**
     * 类型配置：构成展示（1:展示 0:不展示）
     */
    private Integer constituteShow;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

