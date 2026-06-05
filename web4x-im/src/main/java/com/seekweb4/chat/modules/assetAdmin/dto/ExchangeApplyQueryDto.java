package com.seekweb4.chat.modules.assetAdmin.dto;

import lombok.Data;

import java.util.Date;

/**
 * 靓号兑换申请查询DTO
 *
 * @author system
 */
@Data
public class ExchangeApplyQueryDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * IM交易号
     */
    private String transactionNumber;

    /**
     * 靓号
     */
    private String prettyNumber;

    /**
     * 状态：0-发起兑换，1-兑换中，2-兑换成功，3-兑换失败
     */
    private Integer status;

    /**
     * 创建时间开始
     */
    private Date createTimeStart;

    /**
     * 创建时间结束
     */
    private Date createTimeEnd;

    /**
     * 页码（从1开始）
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;
}
