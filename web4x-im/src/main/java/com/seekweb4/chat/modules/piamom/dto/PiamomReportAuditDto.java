package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;

@Data
public class PiamomReportAuditDto {

    private Long id;
    /** 1通过 2驳回 */
    private Integer auditStatus;
    private String auditUserId;
}
