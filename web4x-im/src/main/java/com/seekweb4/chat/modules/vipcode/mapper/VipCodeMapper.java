package com.seekweb4.chat.modules.vipcode.mapper;

import org.apache.ibatis.annotations.Param;
import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.vipcode.entity.VipCode;

import java.util.List;

/**
 * 会员码MAPPER接口
 * @author lixinapp
 * @version 2025-03-24
 */
@Mapper
public interface VipCodeMapper extends BaseMapper<VipCode> {

	/**
	 * 查询未同步且已填写类型的会员码（用于无参同步时自动提交），限制返回条数
	 */
	List<VipCode> findUnsyncedWithTypeLimit(@Param("limit") int limit);

	/**
	 * 批量插入会员码（用于批量生成，提升性能）
	 */
	void insertBatch(@Param("list") List<VipCode> list);

	/**
	 * 按兑换码列表批量更新同步状态
	 */
	void updateSyncStatusByCodes(@Param("codes") List<String> codes, @Param("syncStatus") String syncStatus);
}
