package com.seekweb4.chat.modules.withdrawConfig.service.impl;

import com.seekweb4.chat.common.utils.OrderByUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.withdrawConfig.dto.WithdrawConfigQueryDto;
import com.seekweb4.chat.modules.withdrawConfig.entity.WithdrawConfig;
import com.seekweb4.chat.modules.withdrawConfig.mapper.WithdrawConfigMapper;
import com.seekweb4.chat.modules.withdrawConfig.service.WithdrawConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class WithdrawConfigServiceImpl implements WithdrawConfigService {

    @Resource
    private WithdrawConfigMapper withdrawConfigMapper;

    @Override
    public Page<WithdrawConfig> page(WithdrawConfigQueryDto queryDto) {
        Page<WithdrawConfig> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(OrderByUtils.toUnderscoreColumn(queryDto.getOrderBy()));
        }
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        Long count = withdrawConfigMapper.selectAdminCount(queryDto);
        page.setCount(count);
        List<WithdrawConfig> list = withdrawConfigMapper.selectAdminPageList(queryDto);
        page.setList(list);
        return page;
    }

    @Override
    public WithdrawConfig getById(Long id) {
        return withdrawConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean save(WithdrawConfig config) {
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        return withdrawConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean update(WithdrawConfig config) {
        config.setUpdateTime(new Date());
        return withdrawConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean remove(Long id) {
        // 仅当表中记录数大于 1 时才允许删除，避免删除最后一条配置
        Long total = withdrawConfigMapper.selectAdminCount(new WithdrawConfigQueryDto());
        if (total != null && total <= 1) {
            // 返回 false，由上层 Controller 给出“最后一条不可删除”的提示
            log.warn("尝试删除提现配置最后一条记录，已拦截, id={}", id);
            return false;
        }
        return withdrawConfigMapper.deleteByPrimaryKey(id) > 0;
    }
}

