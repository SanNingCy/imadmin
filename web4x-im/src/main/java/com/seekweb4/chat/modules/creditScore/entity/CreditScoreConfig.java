package com.seekweb4.chat.modules.creditScore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 信用分基础配置，对应表 t_credit_score_config
 */
@Data
public class CreditScoreConfig implements Serializable {

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
     * 初始分
     */
    private BigDecimal initScore;

    /**
     * 会员加成（比例）
     */
    private BigDecimal vipBonusRate;

    /**
     * 靓号加成（比例）
     */
    private BigDecimal lianghaoBonusRate;

    /**
     * 开通信用分 ODIC 价格（旧版展示、余额扣款）
     */
    private BigDecimal price;

    /**
     * 链上开通信用分 USDT 价格（链上支付、新版展示）
     */
    private BigDecimal priceUsdt;

    /**
     * 信用分说明
     */
    private String scoreInfo;
}

