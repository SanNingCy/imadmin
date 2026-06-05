package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 朋友圈 t_piamom_moment
 */
@Data
public class PiamomMoment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String content;
    private String imageUrls;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    /** 被引用次数 */
    private Integer quoteCount;
    private Integer isTop;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date topTime;
    private String quoteRefType;
    private Long quoteRefId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;
    /** 展示状态：1 正常 0 下架隐藏 */
    private Integer status;

    /** 关联用户 idno */
    private String userIdno;
    /** 关联用户昵称 */
    private String userNickname;
}
