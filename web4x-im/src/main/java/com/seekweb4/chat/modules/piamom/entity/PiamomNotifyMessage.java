package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 互动消息 t_piamom_notify_message
 */
@Data
public class PiamomNotifyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String receiverId;
    private String fromUserId;
    private String msgType;
    private String targetType;
    private Long targetId;
    private Long refId;
    private String content;
    private Integer isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String receiverIdno;
    private String receiverNickname;
    private String fromUserIdno;
    private String fromUserNickname;
}
