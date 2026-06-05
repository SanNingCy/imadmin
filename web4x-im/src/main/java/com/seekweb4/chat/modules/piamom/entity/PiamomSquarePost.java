package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 广场帖子 t_piamom_square_post
 */
@Data
public class PiamomSquarePost implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String content;
    private String imageUrls;
    private String video;
    private String type;
    private BigDecimal odicStake;
    private Integer odicStakeStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date stakeRefundTime;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    /** 被引用次数 */
    private Integer quoteCount;
    private String commentGateFlags;
    private Integer commentCreditMin;
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

    private String userIdno;
    private String userNickname;
}
