package com.seekweb4.chat.modules.grouphuanying.entity;

import com.seekweb4.chat.modules.group.entity.Group;
import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 群欢迎语Entity
 * @author lixinapp
 * @version 2025-03-24
 */
@Data
public class GroupHuanying extends DataEntity<GroupHuanying> {
	
	private static final long serialVersionUID = 1L;
    @NotNull(message="群不能为空")
	@ExcelField(title="群", fieldType=Group.class, value="group.name", align=2, sort=1)
	private Group group;		// 群
	@ExcelField(title="文本", align=2, sort=2)
	private String title;		// 文本
	@ExcelField(title="图片", align=2, sort=3)
	private String imgs;		// 图片
	@ExcelField(title="视频", align=2, sort=4)
	private String video;		// 视频
	@ExcelField(title="文件", align=2, sort=5)
	private String pdf;		// 文件
	@ExcelField(title="类型 1：纯文本 2：文本+图片 3：文本+视频 4：文本+文件", dictType="xiaoxi_type", align=2, sort=6)
	private String type;		// 类型 1：纯文本 2：文本+图片 3：文本+视频 4：文本+文件

	private String name;	//文件、视频名称
	private String size;	//文件大小
	private Integer miao;	//视频时长（秒）
	
	public GroupHuanying() {
		super();
	}
	
	public GroupHuanying(String id){
		super(id);
	}
}