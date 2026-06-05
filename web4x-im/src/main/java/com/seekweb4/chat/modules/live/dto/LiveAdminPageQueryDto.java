package com.seekweb4.chat.modules.live.dto;

import lombok.Data;

/**
 * Live 模块通用分页查询 DTO
 */
@Data
public class LiveAdminPageQueryDto {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /** 排序字段（前端可传驼峰，Service 会转下划线） */
    private String orderBy;
}

