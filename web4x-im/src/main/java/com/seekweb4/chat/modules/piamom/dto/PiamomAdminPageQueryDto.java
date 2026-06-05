package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;

/**
 * piamom 后台通用分页查询
 */
@Data
public class PiamomAdminPageQueryDto {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    /** 排序，如 createdAt desc */
    private String orderBy;
}
