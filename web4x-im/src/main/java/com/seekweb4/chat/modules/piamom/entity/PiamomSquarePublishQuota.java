package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 广场发帖信用分额度档位 t_piamom_square_publish_quota
 */
@Data
public class PiamomSquarePublishQuota implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 信用分下限（含） */
    private Integer creditMin;
    /** 信用分上限（含），null 表示无上限 */
    private Integer creditMax;
    /** 每日可发广场帖条数 */
    private Integer dailyLimit;
    /** 排序，越大越优先（区间重叠时） */
    private Integer sortOrder;
    /** 0停用 1启用 */
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;
}
