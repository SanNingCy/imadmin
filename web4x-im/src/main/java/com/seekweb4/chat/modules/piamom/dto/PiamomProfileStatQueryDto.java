package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomProfileStatQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private String userId;
    private String userIdno;
}
