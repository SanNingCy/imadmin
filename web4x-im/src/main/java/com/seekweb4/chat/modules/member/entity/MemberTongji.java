package com.seekweb4.chat.modules.member.entity;

import com.seekweb4.chat.core.persistence.DataEntity;
import lombok.Data;

/**
 * 用户统计
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class MemberTongji extends DataEntity<MemberTongji> {

	private String city;
	private String model;
	private Integer count;

	public MemberTongji() {
		super();
	}

	public MemberTongji(String id){
		super(id);
	}
}