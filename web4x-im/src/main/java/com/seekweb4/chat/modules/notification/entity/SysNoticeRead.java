package com.seekweb4.chat.modules.notification.entity;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.modules.member.entity.Member;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@Alias("ImSysNoticeRead")
public class SysNoticeRead extends DataEntity<SysNoticeRead> {
	private static final long serialVersionUID = 1L;
	private Member u;            // 用户
	private String noticeId;     // 公告ID
	private Integer noticeType;  // 公告类型（1:活动 2:公告 3:动态）
	private Date readTime; 		 // 读取时间
	private String eventId;      // 点击事件ID
}
