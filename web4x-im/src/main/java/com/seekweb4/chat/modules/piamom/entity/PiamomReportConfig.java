package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 举报配置 t_piamom_report_config
 */
@Data
public class PiamomReportConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String reportType;
    private String description;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
}
