package com.seekweb4.chat.modules.memberCharacterTags.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 人设标签管理
 */
@Data
public class MemberCharacterTags implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 人设圈子标签
     */
    private String characterTags;

    /**
     * 标签解释
     */
    private String characterExplanation;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 创建人id
     */
    private String createBy;

    /**
     * 更新人id
     */
    private String updateBy;

    /**
     * 是否删除(0:否 1:是)
     */
    private Integer isDeleted;
}