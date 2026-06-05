package com.seekweb4.chat.modules.assetAdmin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付费率配置查询DTO
 *
 * @author admin
 * @since 2025-10-28
 */
@Data
public class PaymentRateConfigQueryDto {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 类型(1:入金wx 2:提现)
     */
    private Integer paymentType;

    /**
     * 费率
     */
    private BigDecimal rate;

    /**
     * 创建人id
     */
    private String createBy;

    /**
     * 更新人id
     */
    private String updateBy;

    /**
     * 删除标记(0:否 1:是)
     */
    private Integer isDeleted;

    /**
     * 页码
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

    /**
     * 兼容前端传的pageNum参数（前端使用pageNum，后端使用pageNo）
     * 当设置pageNum时，同时设置pageNo
     */
//    public void setPageNum(Integer pageNum) {
//        if (pageNum != null) {
//            this.pageNo = pageNum;
//        }
//    }
}
