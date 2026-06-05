package com.seekweb4.chat.modules.kejian.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seekweb4.chat.modules.kejianzu.entity.KejianZu;
import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

import java.util.Date;

/**
 * 课件Entity
 * @author lixinapp
 * @version 2025-05-24
 */
@Data
public class Kejian extends DataEntity<Kejian> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="发送时间", align=2, sort=1)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date sendTime;		// 发送时间
    @NotNull(message="分组不能为空")
	@ExcelField(title="分组", fieldType=KejianZu.class, value="zu.name", align=2, sort=2)
	private KejianZu zu;		// 分组
	@ExcelField(title="文本", align=2, sort=3)
	private String title;		// 文本
	@ExcelField(title="图片", align=2, sort=4)
	private String imgs;		// 图片
	@ExcelField(title="视频", align=2, sort=5)
	private String video;		// 视频
	@ExcelField(title="语音", align=2, sort=6)
	private String sound;		// 语音
	@ExcelField(title="文件", align=2, sort=7)
	private String pdf;		// 文件
	@ExcelField(title="类型 1：纯文本 2：图片 3：视频 4：文件 5：语音", dictType="kejian_type", align=2, sort=8)
	private String type;		// 类型 1：纯文本 2：图片 3：视频 4：文件 5：语音
	@ExcelField(title="文件、视频名称", align=2, sort=9)
	private String name;		// 文件、视频名称
	@ExcelField(title="文件大小（kb）", align=2, sort=10)
	private String size;		// 文件大小（kb）
	@ExcelField(title="视频时长（秒）/语音时长", align=2, sort=11)
	private Integer miao;		// 视频时长（秒）/语音时长
	
	public Kejian() {
		super();
	}
	
	public Kejian(String id){
		super(id);
	}
}