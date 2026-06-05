package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户获赞记录 t_piamom_user_like_record
 */
@Data
public class PiamomUserLikeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 被点赞用户ID */
    private String userId;
    /** 点赞用户ID */
    private String fromUserId;
    /** moment朋友圈 square广场 comment评论 */
    private String targetType;
    private Long targetId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String userIdno;
    private String userNickname;
    private String fromUserIdno;
    private String fromUserNickname;
}

