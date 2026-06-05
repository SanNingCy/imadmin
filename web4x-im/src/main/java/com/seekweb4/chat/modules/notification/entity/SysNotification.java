package com.seekweb4.chat.modules.notification.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.modules.notification.jackson.TargetUserIdsJsonDeserializer;
import com.seekweb4.chat.modules.notification.jackson.TargetUserIdsJsonSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SysNotification extends DataEntity<SysNotification> {
	private static final long serialVersionUID = 1L;
	private String noticeTitle;     // 公告标题
	private String contentUrl;      // 公告富文本URL
	private String content;      	// 公告富文本
	private String previewContent;  // 预览内容
	private String status;          // 公告状态（0关闭 1正常）
	private String imageUrl;        // 公告缩略图（可选）
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date startTime; 		// 开始时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endTime;   		// 结束时间
	private Integer noticeType;     // 公告类型
	private Integer intervalNumber; // 弹窗时间间隔
	private Integer intervalUnit;   // 时间单位(1:秒 2:分 3:时 4:天)
	private Integer number;         // 通知次数
	private Integer forcePopup;     // 是否强制弹出 (0:不强制，1: 强制)
	/**
	 * 可选。库表 target_user_ids 可为 NULL；null/未传/空数组 均表示不限制用户。
	 * 请求字段 targetUserIds：可传 JSON 数组 ["id1","id2"]，或单个用户 ID 字符串（会存为单元素数组）。
	 */
	@JsonDeserialize(using = TargetUserIdsJsonDeserializer.class)
	@JsonSerialize(using = TargetUserIdsJsonSerializer.class)
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private String targetUserIds;
	/** 仅用于返回展示：由 targetUserIds 映射出的用户 idno 列表（不落库） */
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private List<String> targetUserIdnos;
	/** 列表查询条件：目标用户 idno（模糊） */
	private String targetUserIdno;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
}
