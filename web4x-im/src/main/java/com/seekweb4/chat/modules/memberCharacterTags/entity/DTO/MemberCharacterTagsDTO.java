package com.seekweb4.chat.modules.memberCharacterTags.entity.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class MemberCharacterTagsDTO {

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
    private Date createTime;

    /**
     * 更新时间
     */
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

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 兼容前端传的pageNum参数（前端使用pageNum，后端使用pageNo）
     * 当设置pageNum时，同时设置pageNo
     */
    public void setPageNum(Integer pageNum) {
        if (pageNum != null) {
            this.pageNo = pageNum;
        }
    }

}
