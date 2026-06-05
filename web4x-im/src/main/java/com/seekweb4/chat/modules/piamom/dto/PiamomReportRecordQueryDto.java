package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomReportRecordQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private String reporterId;
    private String reporterIdno;
    private String targetType;
    private Long targetId;
    private Integer auditStatus;
    private Long reportConfigId;
}
