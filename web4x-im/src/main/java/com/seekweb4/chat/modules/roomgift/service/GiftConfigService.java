package com.seekweb4.chat.modules.roomgift.service;

import com.seekweb4.chat.modules.roomgift.entity.GiftConfig;
import com.seekweb4.chat.modules.roomgift.mapper.GiftConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GiftConfigService {
    @Autowired
    private GiftConfigMapper giftConfigMapper;

    /**
     * 获取礼物规则说明
     * @return 规则说明
     */
    public String getGiftGuidelines() {
        GiftConfig config = giftConfigMapper.findEnabledConfig();
        if (config != null) {
            return config.getGiftGuidelines();
        }
        // 默认规则
        return "礼物规则:受赠者将收到后将转换成50%代币";
    }

    /**
     * 获取代币转换比例
     * @return 转换比例
     */
    public Double getConversionRate() {
        GiftConfig config = giftConfigMapper.findEnabledConfig();
        if (config != null && config.getConversionRate() != null) {
            return config.getConversionRate();
        }
        // 默认50%
        return 0.5;
    }

    /**
     * 获取免密支付配置
     * @return 是否免密支付
     */
    public Boolean getIsNonPayPwd() {
        GiftConfig config = giftConfigMapper.findEnabledConfig();
        if (config != null && config.getIsNonPayPwd() != null) {
            return config.getIsNonPayPwd();
        }
        // 默认需要密码
        return false;
    }

    /**
     * 获取配置列表
     * @param config 查询条件
     * @return 配置列表
     */
    public List<GiftConfig> findList(GiftConfig config) {
        return giftConfigMapper.findList(config);
    }

    /**
     * 保存配置
     * @param config 配置信息
     */
    @Transactional(readOnly = false)
    public void save(GiftConfig config) {
        if (config.getId() == null || config.getId().isEmpty()) {
            config.preInsert();
            giftConfigMapper.insert(config);
        } else {
            config.preUpdate();
            giftConfigMapper.update(config);
        }
    }

    /**
     * 保存配置
     * @param config 配置信息
     */
    @Transactional(readOnly = false)
    public void update(GiftConfig config) {
        if (config.getId() != null) {
            config.preUpdate();
            if (config.getConversionRate() != null){
                config.setGiftGuidelines("礼物规则:受赠者将收到后将转换成" + config.getConversionRate() * 100 + "%代币");
            }
            giftConfigMapper.update(config);
        }
    }
}
