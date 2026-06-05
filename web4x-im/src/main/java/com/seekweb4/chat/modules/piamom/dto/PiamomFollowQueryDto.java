package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomFollowQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private String userId;
    private String userIdno;
    private String followerId;
    private String followerIdno;
    private Integer status;
}
