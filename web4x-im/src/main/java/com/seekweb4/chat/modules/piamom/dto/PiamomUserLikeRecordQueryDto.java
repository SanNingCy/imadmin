package com.seekweb4.chat.modules.piamom.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PiamomUserLikeRecordQueryDto extends PiamomAdminPageQueryDto {

    private Long id;
    /** 被点赞用户ID */
    private String userId;
    /** 被点赞用户 idno（模糊匹配） */
    private String userIdno;
    /** 点赞用户ID */
    private String fromUserId;
    /** 点赞用户 idno（模糊匹配） */
    private String fromUserIdno;
    /** moment朋友圈 square广场 comment评论 */
    private String targetType;
    private Long targetId;
}

