package com.seekweb4.chat.modules.piamom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 举报记录 t_piamom_report_record
 */
@Data
public class PiamomReportRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String reporterId;
    private String targetType;
    private Long targetId;
    private Long reportConfigId;
    private String reason;
    private String imageUrls;
    private Integer auditStatus;
    private String auditUserId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    private String reporterIdno;
    private String reporterNickname;
    private String reportTypeName;

    /**
     * 举报附件图片数组（由 imageUrls 解析得到，优先给前端使用）
     */
    private List<String> imageUrlList;

    /**
     * 被举报内容发布者信息
     */
    private String publisherUserId;
    private String publisherIdno;
    private String publisherNickname;

    /**
     * 被举报帖子信息（targetId 对应的业务主键）
     */
    private Long postId;
    private Integer postStatus;
    /** 被举报帖子展示状态：1 正常 0 下架隐藏 */
    private Integer postVisibleStatus;
    private String postContent;
    private String postImageUrls;
    private String postVideo;

    /**
     * 帖子媒体数组（由 postImageUrls/postVideo 解析得到）
     */
    private List<String> postImageUrlList;
    private List<String> postVideoList;
}
