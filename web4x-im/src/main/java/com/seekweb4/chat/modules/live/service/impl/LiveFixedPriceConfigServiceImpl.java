package com.seekweb4.chat.modules.live.service.impl;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigQueryDto;
import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigVo;
import com.seekweb4.chat.modules.live.entity.LiveFixedPriceConfig;
import com.seekweb4.chat.modules.live.mapper.LiveFixedPriceConfigMapper;
import com.seekweb4.chat.modules.live.mapper.LiveTimeDurationConfigMapper;
import com.seekweb4.chat.modules.live.mapper.LiveUserTierConfigMapper;
import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;
import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;
import com.seekweb4.chat.modules.live.service.LiveFixedPriceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class LiveFixedPriceConfigServiceImpl implements LiveFixedPriceConfigService {

    @Resource
    private LiveFixedPriceConfigMapper fixedPriceConfigMapper;

    @Resource
    private LiveTimeDurationConfigMapper durationConfigMapper;

    @Resource
    private LiveUserTierConfigMapper tierConfigMapper;

    /** 仅允许按主表 c 列排序，避免 join 后列名歧义 */
    private static final Set<String> SORTABLE_COLUMNS = new HashSet<>(Arrays.asList(
            "id", "duration_id", "tier_id", "fixed_price", "status",
            "create_time", "update_time", "create_by", "update_by", "remark"
    ));

    @Override
    public Page<LiveFixedPriceConfigVo> page(LiveFixedPriceConfigQueryDto queryDto) {
        Page<LiveFixedPriceConfigVo> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(qualifyOrderByForMainTable(convertOrderByToUnderscore(queryDto.getOrderBy())));
        }
        Long count = fixedPriceConfigMapper.selectAdminCount(queryDto);
        page.setCount(count);
        page.setList(fixedPriceConfigMapper.selectAdminPageList(queryDto));
        return page;
    }

    @Override
    public LiveFixedPriceConfigVo getById(Long id) {
        return fixedPriceConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean create(LiveFixedPriceConfig config) {
        validateRefsAndPrice(config.getDurationId(), config.getTierId(), config.getFixedPrice());
        assertNoDuplicateDurationTier(config.getDurationId(), config.getTierId(), null);
        config.setIsDeleted(0);
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        return fixedPriceConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean update(LiveFixedPriceConfig config) {
        if (config.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        LiveFixedPriceConfigVo existing = fixedPriceConfigMapper.selectByPrimaryKey(config.getId());
        if (existing == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        Long durationId = config.getDurationId() != null ? config.getDurationId() : existing.getDurationId();
        Long tierId = config.getTierId() != null ? config.getTierId() : existing.getTierId();
        BigDecimal price = config.getFixedPrice() != null ? config.getFixedPrice() : existing.getFixedPrice();
        validateRefsAndPrice(durationId, tierId, price);
        if (config.getDurationId() != null || config.getTierId() != null) {
            assertNoDuplicateDurationTier(durationId, tierId, config.getId());
        }
        config.setUpdateTime(new Date());
        return fixedPriceConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean delete(Long id, String updateBy) {
        LiveFixedPriceConfig config = new LiveFixedPriceConfig();
        config.setId(id);
        config.setIsDeleted(1);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return fixedPriceConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    private void validateRefsAndPrice(Long durationId, Long tierId, BigDecimal fixedPrice) {
        if (durationId == null) {
            throw new IllegalArgumentException("会议室时长不能为空");
        }
        if (tierId == null) {
            throw new IllegalArgumentException("会议室人数档位不能为空");
        }
        if (fixedPrice == null) {
            throw new IllegalArgumentException("固定总价不能为空");
        }
        if (fixedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("固定总价必须大于 0");
        }
        LiveTimeDurationConfig duration = durationConfigMapper.selectByPrimaryKey(durationId);
        if (duration == null || (duration.getIsDeleted() != null && duration.getIsDeleted() == 1)) {
            throw new IllegalArgumentException("会议时长配置不存在");
        }
        LiveUserTierConfig tier = tierConfigMapper.selectByPrimaryKey(tierId);
        if (tier == null || (tier.getIsDeleted() != null && tier.getIsDeleted() == 1)) {
            throw new IllegalArgumentException("人数档位配置不存在");
        }
    }

    private void assertNoDuplicateDurationTier(Long durationId, Long tierId, Long excludeId) {
        if (fixedPriceConfigMapper.countByDurationAndTier(durationId, tierId, excludeId) > 0) {
            throw new IllegalArgumentException("该时长与人数档位组合已存在固定价格配置");
        }
    }

    private String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            part = part.trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            String[] fieldAndOrder = part.split("\\s+");
            if (fieldAndOrder.length == 0) {
                continue;
            }
            String fieldName = fieldAndOrder[0].trim();
            String underscoreField;
            if (fieldName.contains("_")) {
                underscoreField = fieldName;
            } else {
                underscoreField = StringUtils.toUnderScoreCase(fieldName);
            }
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(underscoreField);
            if (fieldAndOrder.length > 1) {
                result.append(" ").append(fieldAndOrder[1].trim());
            }
        }
        return result.toString();
    }

    private String qualifyOrderByForMainTable(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        String[] parts = orderBy.split(",");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            part = part.trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            String[] fieldAndOrder = part.split("\\s+");
            String col = fieldAndOrder[0].trim();
            if (col.startsWith("c.")) {
                col = col.substring(2);
            }
            if (!SORTABLE_COLUMNS.contains(col)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("c.").append(col);
            if (fieldAndOrder.length > 1) {
                String dir = fieldAndOrder[1].trim().toLowerCase();
                if ("asc".equals(dir) || "desc".equals(dir)) {
                    sb.append(" ").append(dir);
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }
}
