package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomCommentQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private Long momentId;
    private Long squareId;
    private String userId;
    private String userIdno;
    private String content;
}
