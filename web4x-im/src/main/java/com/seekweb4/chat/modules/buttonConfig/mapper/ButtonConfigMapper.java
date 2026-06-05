package com.seekweb4.chat.modules.buttonConfig.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 按钮配置Mapper
 *
 * @author system
 * @since 2025-11-28
 */
@Mapper
public interface ButtonConfigMapper extends BaseMapper<ButtonConfig> {

    /**
     * 根据按钮标识查询按钮配置
     * @param buttonKey 按钮标识(transfer:转账 withdraw:提现)
     * @return 按钮配置
     */
    ButtonConfig selectByButtonKey(@Param("buttonKey") String buttonKey);

    int updateKey(ButtonConfig buttonConfig);
}
