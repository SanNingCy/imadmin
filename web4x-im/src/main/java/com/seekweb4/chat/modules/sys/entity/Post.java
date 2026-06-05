package com.seekweb4.chat.modules.sys.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;

/**
 * 岗位Entity
 */
public class Post extends DataEntity<Post> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 岗位名称
	private String code;		// 岗位编码
	private String type;		// 岗位类型
	private String status;		// 岗位状态
	private Integer sort;		// 岗位排序
	
	public Post() {
		super();
	}

	public Post(String id){
		super(id);
	}

	@ExcelField(title="岗位名称", align=2, sort=1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ExcelField(title="岗位编码", align=2, sort=2)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@ExcelField(title="岗位类型", dictType="sys_post_type", align=2, sort=3)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@ExcelField(title="岗位状态", dictType="yes_no", align=2, sort=4)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@NotNull(message="岗位排序不能为空")
	@ExcelField(title="岗位排序", align=2, sort=5)
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
}