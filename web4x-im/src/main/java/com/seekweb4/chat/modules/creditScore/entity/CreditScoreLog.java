package com.seekweb4.chat.modules.creditScore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 信用分日志，对应表 t_credit_score_log（列 desc 映射为 logDesc）
 */
@Data
public class CreditScoreLog implements Serializable {

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

    private BigDecimal score;

    private String userId;

    /** 对应数据库列 `desc` */
    private String logDesc;

    /**
     * 备注（后台加减分：系统增加信用分 / 系统扣减信用分）
     */
    private String remark;

    private BigDecimal vipBonusRate;
    private BigDecimal lianghaoBonusRate;
    private BigDecimal baseScore;
}
