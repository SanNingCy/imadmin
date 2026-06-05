package com.seekweb4.chat.modules.roomgift.mapper;

import com.seekweb4.chat.modules.roomgift.entity.Gift;
import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftMapper extends BaseMapper<Gift> {
    /**
     * 获取启用的礼物列表，按排序字段排序
     * @return 礼物列表
     */
    List<Gift> findEnabledGifts();

    /**
     * 根据类型获取礼物列表
     * @param type 礼物类型
     * @return 礼物列表
     */
    List<Gift> findGiftsByType(@Param("type") String type);

    /**
     * 根据ID获取礼物
     * @param id 礼物ID
     * @return 礼物信息
     */
    Gift get(@Param("id") String id);

    /**
     * 获取礼物列表
     * @param gift 查询条件
     * @return 礼物列表
     */
    List<Gift> findList(Gift gift);

    /**
     * 插入礼物
     * @param gift 礼物信息
     * @return 影响行数
     */
    int insert(Gift gift);

    /**
     * 更新礼物
     * @param gift 礼物信息
     * @return 影响行数
     */
    int update(Gift gift);

    /**
     * 删除礼物
     * @param gift 礼物信息
     * @return 影响行数
     */
    int delete(Gift gift);

    /**
     * 分页查询礼物列表
     * @param gift 查询条件
     * @return 礼物分页列表
     */
    List<Gift> findPage(Gift gift);
}
