package com.seekweb4.chat.modules.roomgift.entity;

import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import com.seekweb4.chat.core.persistence.DataEntity;
import lombok.Data;

/**
 * 礼物配置Entity
 */
@Data
public class Gift extends DataEntity<Gift> {

    @ExcelField(title="礼物ID", align=2, sort=0)
    private String id;		// 礼物ID

    @ExcelField(title="礼物名称", align=2, sort=1)
    private String name;		// 礼物名称

    @ExcelField(title="礼物图片", align=2, sort=2)
    private String img;		// 礼物图片URL

    @ExcelField(title="礼物价值", align=2, sort=3)
    private Integer value;		// 礼物价值（代币数量）

    @ExcelField(title="排序", align=2, sort=4)
    private Integer sort;		// 排序

    @ExcelField(title="状态", dictType="yes_no", align=2, sort=5)
    private String status;		// 状态 0：禁用 1：启用

    @ExcelField(title="礼物类型", dictType="gift_type", align=2, sort=6)
    private String type;		// 礼物类型 1：免费 2：付费

    @ExcelField(title="描述", align=2, sort=7)
    private String description;	// 礼物描述
}
