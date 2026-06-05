package com.seekweb4.chat.modules.roomgift.entity;

import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import com.seekweb4.chat.core.persistence.DataEntity;
import lombok.Data;

/**
 * 礼物配置
 */
@Data
public class GiftConfig extends DataEntity<GiftConfig> {
    @ExcelField(title="配置ID", align=2, sort=0)
    private String id;		// 配置ID

    @ExcelField(title="配置名称", align=2, sort=1)
    private String configName;		// 配置名称

    @ExcelField(title="礼物规则", align=2, sort=2)
    private String giftGuidelines;	// 礼物规则说明

    @ExcelField(title="代币转换比例", align=2, sort=3)
    private Double conversionRate;	// 代币转换比例（如0.5表示50%）

    @ExcelField(title="状态", dictType="yes_no", align=2, sort=4)
    private String status;		// 状态 0：禁用 1：启用

    @ExcelField(title="是否免密支付", align=2, sort=4)
    private Boolean isNonPayPwd;		// 创建时间

    @ExcelField(title="备注", align=2, sort=5)
    private String remark;		// 备注
}
