package com.seekweb4.chat.modules.live.service.impl;

import com.seekweb4.chat.common.utils.IdGen;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveOrderCreateReq;
import com.seekweb4.chat.modules.live.dto.LiveOrderRecordQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveOrderRecord;
import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;
import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;
import com.seekweb4.chat.modules.live.mapper.LiveOrderRecordMapper;
import com.seekweb4.chat.modules.live.mapper.LiveTimeDurationConfigMapper;
import com.seekweb4.chat.modules.live.mapper.LiveUserTierConfigMapper;
import com.seekweb4.chat.modules.live.service.LiveOrderService;
import com.seekweb4.chat.modules.live.service.LiveUsdtMeetingPriceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class LiveOrderServiceImpl implements LiveOrderService {

    @Resource
    private LiveOrderRecordMapper orderRecordMapper;

    @Resource
    private LiveTimeDurationConfigMapper durationConfigMapper;

    @Resource
    private LiveUserTierConfigMapper tierConfigMapper;

    @Resource
    private LiveUsdtMeetingPriceConfigService usdtMeetingPriceConfigService;

    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = new HashSet<>();

    @Override
    public Page<LiveOrderRecord> page(LiveOrderRecordQueryDto queryDto) {
        Page<LiveOrderRecord> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(convertOrderByToUnderscore(queryDto.getOrderBy()));
        }
        Long count = orderRecordMapper.selectAdminCount(queryDto);
        page.setCount(count);
        List<LiveOrderRecord> list = orderRecordMapper.selectAdminPageList(queryDto);
        for (LiveOrderRecord record : list) {
            enrichUsdtTotalAmount(record);
        }
        page.setList(list);
        return page;
    }

    @Override
    public LiveOrderRecord getById(Long id) {
        return orderRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public LiveOrderRecord getByOrderNo(String orderNo) {
        return orderRecordMapper.selectByOrderNo(orderNo);
    }

    @Override
    public LiveOrderRecord createOrder(LiveOrderCreateReq req) {
        LiveTimeDurationConfig duration = durationConfigMapper.selectByPrimaryKey(req.getDurationId());
        if (duration == null || duration.getIsDeleted() != null && duration.getIsDeleted() == 1) {
            throw new IllegalArgumentException("会议时长配置不存在");
        }
        if (duration.getStatus() == null || duration.getStatus() != 1) {
            throw new IllegalArgumentException("会议时长配置已禁用");
        }

        LiveUserTierConfig tier = tierConfigMapper.selectByPrimaryKey(req.getTierId());
        if (tier == null || tier.getIsDeleted() != null && tier.getIsDeleted() == 1) {
            throw new IllegalArgumentException("人数档位配置不存在");
        }
        if (tier.getStatus() == null || tier.getStatus() != 1) {
            throw new IllegalArgumentException("人数档位配置已禁用");
        }

        BigDecimal amount = usdtMeetingPriceConfigService.resolveSalePriceUsdt(req.getDurationId(), req.getTierId());

        LiveOrderRecord record = new LiveOrderRecord();
        record.setOrderNo(IdGen.getOrderNo());
        record.setUserId(req.getUserId());
        record.setDurationId(req.getDurationId());
        record.setDurationValue(duration.getDurationValue() == null ? null : duration.getDurationValue().longValue());
        record.setTierId(req.getTierId());
        record.setTierValue(tier.getTierValue() == null ? null : tier.getTierValue().longValue());
        record.setTotalAmount(amount);
        record.setGroupIdNo(req.getGroupIdNo());
        record.setLiveStatus("pending_create");
        record.setBeginTime(req.getBeginTime());
        record.setEndTime(req.getEndTime());
        record.setChannelName(req.getChannelName());
        record.setChannelId(req.getChannelId());
        record.setRemark(req.getRemark());
        record.setCreateBy(req.getCreateBy());
        record.setUpdateBy(req.getCreateBy());
        record.setIsDeleted(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        int rows = orderRecordMapper.insert(record);
        if (rows <= 0) {
            throw new IllegalStateException("创建订单失败");
        }
        return record;
    }

    @Override
    public boolean updateStatus(Long id, String liveStatus, String updateBy) {
        LiveOrderRecord record = new LiveOrderRecord();
        record.setId(id);
        record.setLiveStatus(liveStatus);
        record.setUpdateBy(updateBy);
        record.setUpdateTime(new Date());
        return orderRecordMapper.updateByPrimaryKeySelective(record) > 0;
    }

    @Override
    public boolean delete(Long id, String updateBy) {
        LiveOrderRecord record = new LiveOrderRecord();
        record.setId(id);
        record.setIsDeleted(1);
        record.setUpdateBy(updateBy);
        record.setUpdateTime(new Date());
        return orderRecordMapper.updateByPrimaryKeySelective(record) > 0;
    }

    private String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();
        for (String p : parts) {
            String part = p == null ? "" : p.trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            String[] fieldAndOrder = part.split("\\s+");
            String fieldName = fieldAndOrder[0].trim();
            String underscoreField;
            if (fieldName.contains("_") || CAMEL_CASE_COLUMN_WHITELIST.contains(fieldName)) {
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

    private void enrichUsdtTotalAmount(LiveOrderRecord record) {
        if (record == null || record.getDurationId() == null || record.getTierId() == null) {
            return;
        }
        BigDecimal usdtAmount = usdtMeetingPriceConfigService.findSalePriceUsdt(record.getDurationId(), record.getTierId());
        if (usdtAmount != null) {
            record.setTotalAmount(usdtAmount);
        }
    }
}

