package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 粉丝拉黑 t_piamom_user_blacklist
 */
@Data
public class PiamomUserBlacklist implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String blackUserId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String userIdno;
    private String userNickname;
    private String blackUserIdno;
    private String blackUserNickname;
}
