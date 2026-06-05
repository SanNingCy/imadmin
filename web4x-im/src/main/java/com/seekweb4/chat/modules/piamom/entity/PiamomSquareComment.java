package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 广场评论 t_piamom_square_comment
 */
@Data
public class PiamomSquareComment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long squareId;
    private String userId;
    private Long parentId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String userIdno;
    private String userNickname;
}
