package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomSquareLikeQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private Long squareId;
    private String userId;
    /** 点赞人 idno（模糊匹配） */
    private String userIdno;
}

