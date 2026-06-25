package com.seekweb4.chat.modules.live.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会议订单记录 t_live_order_record
 */
@Data
public class LiveOrderRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderNo;

    private String userId;

    /** 用户 idno（关联 t_member，仅列表查询带出） */
    private String userIdno;

    private Long durationId;

    /** 会议时长(分钟) */
    private Long durationValue;

    private Long tierId;

    /** 人数上限 */
    private Long tierValue;

    private BigDecimal totalAmount;

    /** 本段会议花费的 ODIC 金额（支付时快照，未支付为空） */
    private BigDecimal odicAmount;

    private String groupIdNo;

    /** 群展示 idno：按 groupIdNo 匹配 t_group.id（或兼容误存为 idno）；含已逻辑删除群；与 channelId（会议室）无关 */
    private String tidnoGroup;

    /** pending_create / active / destroyed */
    private String liveStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private String channelName;

    private String channelId;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Integer isDeleted;
}

