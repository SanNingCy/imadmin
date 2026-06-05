package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户主页统计 t_piamom_user_profile_stat
 */
@Data
public class PiamomUserProfileStat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private Integer followerCount;
    private Integer followingCount;
    private Integer likeCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;

    private String userIdno;
    private String userNickname;
}
