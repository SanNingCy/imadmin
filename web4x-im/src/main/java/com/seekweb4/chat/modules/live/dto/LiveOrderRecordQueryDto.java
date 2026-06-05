package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LiveOrderRecordQueryDto extends LiveAdminPageQueryDto {
    private Long id;
    private String orderNo;
    private String userId;
    /** 按用户 idno 模糊查询 */
    private String userIdno;
    private Long durationId;
    private Long tierId;
    private String groupIdNo;
    /** 按群 idno 模糊查询（与返回字段 tidnoGroup 对应，入参名用 groupIdno） */
    private String groupIdno;
    /** 按群 idno 模糊查询（与返回字段 tidnoGroup 对应，入参名用 groupIdno） */
    private String tidnoGroup;
    private String liveStatus;
    private Date beginTimeStart;
    private Date beginTimeEnd;
}

