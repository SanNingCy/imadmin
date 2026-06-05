package com.seekweb4.chat.modules.vipcode.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员码同步到链桥 - 单条数据项（与链桥 syncFromIm 入参 data 元素一致）
 */
@Data
public class SyncFromImItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 兑换码 */
    private String code;
    /** 会员天数 */
    private Integer day;
    /** 编号（链桥 idNo） */
    private Integer idNo;
    /** 类型（链桥 type） */
    private Integer type;
}
