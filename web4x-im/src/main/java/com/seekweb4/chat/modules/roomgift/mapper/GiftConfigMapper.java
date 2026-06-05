package com.seekweb4.chat.modules.roomgift.mapper;

import com.seekweb4.chat.modules.roomgift.entity.GiftConfig;
import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftConfigMapper extends BaseMapper<GiftConfig> {
    /**
     * 获取启用的礼物配置
     * @return 礼物配置
     */
    GiftConfig findEnabledConfig();

    /**
     * 根据ID获取配置
     * @param id 配置ID
     * @return 配置信息
     */
    GiftConfig get(@Param("id") String id);

    /**
     * 获取配置列表
     * @param config 查询条件
     * @return 配置列表
     */
    List<GiftConfig> findList(GiftConfig config);

    /**
     * 插入配置
     * @param config 配置信息
     * @return 影响行数
     */
    int insert(GiftConfig config);

    /**
     * 更新配置
     * @param config 配置信息
     * @return 影响行数
     */
    int update(GiftConfig config);

    /**
     * 删除配置
     * @param config 配置信息
     * @return 影响行数
     */
    int delete(GiftConfig config);
}
