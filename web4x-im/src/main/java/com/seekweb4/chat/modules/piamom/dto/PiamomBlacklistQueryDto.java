package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomBlacklistQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private String userId;
    private String userIdno;
    private String blackUserId;
    private String blackUserIdno;
}
