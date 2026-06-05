package com.seekweb4.chat.modules.PaymentTransaction.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class TransactionPageRequestDTO implements Serializable {


    private String userId;


    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 计算偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
