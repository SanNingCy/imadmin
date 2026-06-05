package com.seekweb4.chat.modules.signset.service;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.signset.entity.SignSetItem;
import com.seekweb4.chat.modules.signset.mapper.SignSetItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Slf4j
@Service
@Transactional
public class SignSetItemService {

    @Resource
    private SignSetItemMapper signSetItemMapper;

    /**
     * 新增连签额外奖励配置
     */
    public boolean add(SignSetItem item) {
        if (item == null || item.getSid() == null || StringUtils.isBlank(item.getSid().getId())) {
            throw new IllegalArgumentException("签到规则ID(sid)不能为空");
        }
        item.preInsert();
        int rows = signSetItemMapper.insert(item);
        log.info("新增连签额外奖励配置, itemId={}, rows={}", item.getId(), rows);
        return rows > 0;
    }

    /**
     * 修改连签额外奖励配置
     */
    public boolean update(SignSetItem item) {
        if (item == null || StringUtils.isBlank(item.getId())) {
            throw new IllegalArgumentException("主键ID不能为空");
        }
        item.preUpdate();
        int rows = signSetItemMapper.update(item);
        log.info("修改连签额外奖励配置, itemId={}, rows={}", item.getId(), rows);
        return rows > 0;
    }

    /**
     * 删除连签额外奖励配置（物理删除，保持与原有逻辑一致）
     */
    public boolean deleteById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("主键ID不能为空");
        }
        SignSetItem item = new SignSetItem(id);
        int rows = signSetItemMapper.delete(item);
        log.info("删除连签额外奖励配置, itemId={}, rows={}", id, rows);
        return rows > 0;
    }
}

