package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 热门及广场配置 t_piamom_hot_config
 */
@Data
public class PiamomHotConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer likeThreshold;
    private Integer viewThreshold;
    private BigDecimal totalOdicStake;
    private String stakeTip;
    private String stakeRuleTitle;
    private String stakeRuleContent;
    private String auditRuleTitle;
    private String auditRuleContent;
    /** 举报页投诉说明全文 */
    private String complaintNotice;
    /** 发布广场最低信用分（含） */
    private Integer creditMin;
    /** 发布广场最高信用分（含），null 表示无上限 */
    private Integer creditMax;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;
}
