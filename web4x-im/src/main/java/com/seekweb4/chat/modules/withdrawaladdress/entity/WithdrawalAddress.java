package com.seekweb4.chat.modules.withdrawaladdress.entity;

import lombok.Data;

import java.util.Date;

/**
 * 提币常用地址 Entity，对应表 t_withdrawal_address
 */
@Data
public class WithdrawalAddress {

    private Integer id;

    private Date createTime;
    private Date updateTime;

    /**
     * 用户id，对应 user_id
     */
    private String userId;

    /**
     * 入账地址，对应 to_address
     */
    private String toAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否默认 0=否 1=是
     */
    private Integer isDefault;

    /**
     * 地址状态：1=正常 2=审核中 3=异常(黑名单)
     */
    private Integer type;

    /**
     * 地址类型(1:USDT地址 2:代币地址)
     */
    private Integer addressType;

    /**
     * 是否删除：0=已删除 1=未删除
     */
    private Integer flag;
}

