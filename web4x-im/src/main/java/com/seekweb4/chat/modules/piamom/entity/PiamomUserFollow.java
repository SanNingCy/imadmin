package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关注 t_piamom_user_follow
 */
@Data
public class PiamomUserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String followerId;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String userIdno;
    private String userNickname;
    private String followerIdno;
    private String followerNickname;
}
