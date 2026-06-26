package com.seekweb4.chat.modules.vipOpenPlan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员链上开通套餐，表 t_vip_open_plan
 */
@Data
public class VipOpenPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 套餐名称，如月卡/年卡 */
    private String planName;

    /** 开通/续费会员天数 */
    private Integer durationDays;

    /** USDT 价格（链上实扣） */
    private BigDecimal price;

    /** 0停用 1启用 */
    private Integer status;

    /** 排序，越小越靠前 */
    private Integer sortOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    /** 是否删除（0:否 1:是） */
    private Integer isDeleted;
}
