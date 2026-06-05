package com.seekweb4.chat.modules.kejianzugroup.entity;

import com.seekweb4.chat.modules.kejianzu.entity.KejianZu;
import jakarta.validation.constraints.NotNull;
import com.seekweb4.chat.modules.group.entity.Group;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 课件分组群组关联Entity
 * @author lixinapp
 * @version 2025-05-24
 */
@Data
public class KejianZuGroup extends DataEntity<KejianZuGroup> {
	
	private static final long serialVersionUID = 1L;
    @NotNull(message="课件组不能为空")
	@ExcelField(title="课件组", fieldType=KejianZu.class, value="zu.name", align=2, sort=1)
	private KejianZu zu;		// 课件组
    @NotNull(message="群组不能为空")
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=2)
	private Group group;		// 群组
	
	public KejianZuGroup() {
		super();
	}
	
	public KejianZuGroup(String id){
		super(id);
	}
}