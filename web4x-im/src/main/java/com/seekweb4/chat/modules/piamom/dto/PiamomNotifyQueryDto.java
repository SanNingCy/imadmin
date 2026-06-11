package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomNotifyQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    private String receiverId;
    private String receiverIdno;
    private String fromUserId;
    private String fromUserIdno;
    private String msgType;
    private String targetType;
    private Integer isRead;
}
