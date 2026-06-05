package com.seekweb4.chat.modules.WithdrawApply.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import com.seekweb4.chat.modules.member.entity.Member;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 提现申请表
 *
 * @author system
 * @since 2025-10-24
 */
@Data
public class WithdrawApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 提现交易号
     */
    private String transactionNumber;

    /**
     * 种类(1:积分 2:代币)
     */
    private Integer coinId;

    /**
     * 实际提现金额
     */
    private BigDecimal actualAmount;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 费率金额
     */
    private BigDecimal rateAmount;

    /**
     * 提现id(外部)
     */
    private Long withdrawalId;

    /**
     * 提现状态(0:发起提现 1:正在提现 2:提现成功 3:提现失败)
     */
    private Integer status;

    /**
     * 收款地址
     */
    private String receivingAddress;


    /**
     * 提现hash
     */
    private String withdrawalHash;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人id
     */
    private String createBy;

    /**
     * 更新人id
     */
    private String updateBy;

    /**
     * 是否删除(0:否 1:是)
     */
    private Integer isDeleted;

    /**
     * 会员昵称
     */
    private String nickname;

    /**
     * 会员ID号
     */
    private String idno;


    private String remark;

    @ExcelField(title="用户", fieldType= Member.class, value="u.idno,u.nickname", align=2, sort=1)
    private Member member;
}