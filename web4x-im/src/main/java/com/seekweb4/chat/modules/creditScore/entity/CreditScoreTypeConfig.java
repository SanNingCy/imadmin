package com.seekweb4.chat.modules.creditScore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 信用分类型配置，对应表 t_credit_score_type_config
 */
@Data
public class CreditScoreTypeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    /**
     * 是否删除（0:否1:是）
     */
    private Integer isDeleted;

    /**
     * 信用分类型
     */
    private Integer type;

    /**
     * 子类型
     */
    private Integer subtype;

    /**
     * 分数获取上限
     */
    private BigDecimal maxLimit;

    /**
     * 单次获得分数
     */
    private BigDecimal score;

    /**
     * 状态 1:启用 0:禁用
     */
    private Integer status;

    /**
     * 排序值,值越大越靠前
     */
    private Integer orderNum;

    /**
     * 构成展示（1:展示 0:不展示）
     */
    private Integer constituteShow;
}

