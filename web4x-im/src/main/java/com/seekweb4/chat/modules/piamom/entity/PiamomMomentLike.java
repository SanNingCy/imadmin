package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 朋友圈点赞记录 t_piamom_moment_like
 */
@Data
public class PiamomMomentLike implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long momentId;
    private String userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String userIdno;
    private String userNickname;
}

