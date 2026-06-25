package com.seekweb4.chat.modules.live.service.impl;



import com.seekweb4.chat.common.utils.StringUtils;

import com.seekweb4.chat.core.persistence.Page;

import com.seekweb4.chat.modules.live.constant.LivePricingMode;

import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigQueryDto;

import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigVo;

import com.seekweb4.chat.modules.live.dto.LiveUsdtFixedPriceVo;

import com.seekweb4.chat.modules.live.dto.LiveUsdtMeetingPriceBatchSaveReq;

import com.seekweb4.chat.modules.live.dto.LiveUsdtMeetingPriceQueryDto;

import com.seekweb4.chat.modules.live.entity.LiveFixedPriceConfig;

import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;

import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;

import com.seekweb4.chat.modules.live.mapper.LiveFixedPriceConfigMapper;

import com.seekweb4.chat.modules.live.mapper.LiveTimeDurationConfigMapper;

import com.seekweb4.chat.modules.live.mapper.LiveUserTierConfigMapper;

import com.seekweb4.chat.modules.live.service.LiveConfigService;
import com.seekweb4.chat.modules.live.service.LiveFixedPriceConfigService;
import com.seekweb4.chat.modules.live.service.LiveUsdtMeetingPriceConfigService;

import com.seekweb4.chat.modules.live.util.LiveUsdtPriceCalculator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import jakarta.annotation.Resource;

import java.math.BigDecimal;

import java.util.ArrayList;

import java.util.Date;

import java.util.HashSet;

import java.util.List;

import java.util.Set;



@Slf4j

@Service

@Transactional

public class LiveUsdtMeetingPriceConfigServiceImpl implements LiveUsdtMeetingPriceConfigService {



    @Resource

    private LiveFixedPriceConfigService fixedPriceConfigService;

    @Resource

    private LiveFixedPriceConfigMapper fixedPriceConfigMapper;



    @Resource

    private LiveConfigService configService;

    @Resource

    private LiveTimeDurationConfigMapper durationConfigMapper;



    @Resource

    private LiveUserTierConfigMapper tierConfigMapper;



    @Override

    public Page<LiveUsdtFixedPriceVo> page(LiveUsdtMeetingPriceQueryDto queryDto) {

        LiveFixedPriceConfigQueryDto fixedQuery = new LiveFixedPriceConfigQueryDto();

        fixedQuery.setPageNo(queryDto.getPageNo());

        fixedQuery.setPageSize(queryDto.getPageSize());

        fixedQuery.setOrderBy(queryDto.getOrderBy());

        fixedQuery.setId(queryDto.getId());

        fixedQuery.setDurationId(queryDto.getDurationId());

        fixedQuery.setTierId(queryDto.getTierId());

        fixedQuery.setStatus(queryDto.getStatus());

        fixedQuery.setPricingMode(LivePricingMode.USDT);

        Page<LiveFixedPriceConfigVo> rawPage = fixedPriceConfigService.page(fixedQuery);
        Page<LiveUsdtFixedPriceVo> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        page.setCount(rawPage.getCount());
        List<LiveUsdtFixedPriceVo> voList = new ArrayList<>();
        if (rawPage.getList() != null) {
            for (LiveFixedPriceConfigVo item : rawPage.getList()) {
                voList.add(toVo(item));
            }
        }
        page.setList(voList);
        return page;

    }



    @Override

    public LiveUsdtFixedPriceVo getById(Long id) {

        LiveFixedPriceConfigVo vo = fixedPriceConfigMapper.selectByPrimaryKey(id);

        if (vo == null || !LivePricingMode.USDT.equals(vo.getPricingMode())) {

            return null;

        }

        return toVo(vo);

    }



    @Override

    public boolean batchCreate(LiveUsdtMeetingPriceBatchSaveReq req) {

        if (req == null) {

            throw new IllegalArgumentException("请求不能为空");

        }

        Long durationId = resolveOrCreateDuration(req.getDurationId(), req.getDurationMinutes());

        List<LiveUsdtMeetingPriceBatchSaveReq.TierItem> tiers = req.getTiers();

        if (tiers == null || tiers.isEmpty()) {

            throw new IllegalArgumentException("请至少添加一条人数价格配置");

        }

        int status = req.getStatus() != null ? req.getStatus() : 1;

        Set<Long> tierIds = new HashSet<>();

        Set<Integer> peopleCounts = new HashSet<>();

        for (LiveUsdtMeetingPriceBatchSaveReq.TierItem tier : tiers) {

            if (tier.getSalePriceUsdt() == null || tier.getSalePriceUsdt().compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException("会议价格必须大于0");

            }

            Long tierId = resolveOrCreateTier(tier.getTierId(), tier.getPeopleCount());

            if (!tierIds.add(tierId)) {

                throw new IllegalArgumentException("同一批次不能重复配置相同人数");

            }

            LiveUserTierConfig tierConfig = tierConfigMapper.selectByPrimaryKey(tierId);

            if (tierConfig != null && tierConfig.getTierValue() != null && !peopleCounts.add(tierConfig.getTierValue())) {

                throw new IllegalArgumentException("同一批次不能重复配置相同人数");

            }

            assertNoDuplicate(durationId, tierId, null);



            LiveFixedPriceConfig config = new LiveFixedPriceConfig();

            config.setDurationId(durationId);

            config.setTierId(tierId);

            config.setFixedPrice(tier.getSalePriceUsdt());

            config.setPricingMode(LivePricingMode.USDT);

            config.setStatus(status);

            config.setRemark(req.getRemark());

            config.setCreateBy(req.getCreateBy());

            config.setIsDeleted(0);

            config.setCreateTime(new Date());

            config.setUpdateTime(new Date());

            fixedPriceConfigMapper.insert(config);

        }

        return true;

    }



    @Override

    public boolean update(LiveUsdtFixedPriceVo config) {

        if (config == null || config.getId() == null) {

            throw new IllegalArgumentException("id 不能为空");

        }

        LiveFixedPriceConfigVo existing = fixedPriceConfigMapper.selectByPrimaryKey(config.getId());

        if (existing == null || !LivePricingMode.USDT.equals(existing.getPricingMode())) {

            throw new IllegalArgumentException("记录不存在");

        }

        Long durationId = resolveOrCreateDuration(
                config.getDurationId(),
                config.getDurationMinutes() != null ? config.getDurationMinutes() : existing.getDurationValue());

        Long tierId = resolveOrCreateTier(
                config.getTierId(),
                config.getPeopleCount() != null ? config.getPeopleCount() : existing.getTierValue());

        BigDecimal salePrice = config.getSalePriceUsdt() != null ? config.getSalePriceUsdt() : existing.getFixedPrice();

        if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("会议价格必须大于0");

        }

        if (!durationId.equals(existing.getDurationId()) || !tierId.equals(existing.getTierId())) {

            assertNoDuplicate(durationId, tierId, config.getId());

        }



        LiveFixedPriceConfig update = new LiveFixedPriceConfig();

        update.setId(config.getId());

        update.setDurationId(durationId);

        update.setTierId(tierId);

        update.setFixedPrice(salePrice);

        update.setPricingMode(LivePricingMode.USDT);

        if (config.getStatus() != null) {

            update.setStatus(config.getStatus());

        }

        if (config.getRemark() != null) {

            update.setRemark(config.getRemark());

        }

        update.setUpdateTime(new Date());

        return fixedPriceConfigMapper.updateByPrimaryKeySelective(update) > 0;

    }



    @Override

    public boolean delete(Long id, String updateBy) {

        LiveFixedPriceConfigVo existing = fixedPriceConfigMapper.selectByPrimaryKey(id);

        if (existing == null || !LivePricingMode.USDT.equals(existing.getPricingMode())) {

            throw new IllegalArgumentException("记录不存在");

        }

        LiveFixedPriceConfig config = new LiveFixedPriceConfig();

        config.setId(id);

        config.setIsDeleted(1);

        config.setUpdateBy(updateBy);

        config.setUpdateTime(new Date());

        return fixedPriceConfigMapper.updateByPrimaryKeySelective(config) > 0;

    }



    @Override

    public BigDecimal previewCost(int durationMinutes, int peopleCount) {

        return LiveUsdtPriceCalculator.calcCostPriceUsdt(durationMinutes, peopleCount);

    }



    @Override

    public BigDecimal resolveSalePriceUsdt(Long durationId, Long tierId) {

        BigDecimal price = findSalePriceUsdt(durationId, tierId);

        if (price == null) {

            throw new IllegalStateException("未配置该时长与人数档位的 USDT 会议价格");

        }

        return price;

    }



    @Override

    public BigDecimal findSalePriceUsdt(Long durationId, Long tierId) {

        if (durationId == null || tierId == null) {

            return null;

        }

        LiveFixedPriceConfig config = fixedPriceConfigMapper.selectActiveUsdtByDurationAndTier(durationId, tierId);

        if (config == null || config.getFixedPrice() == null) {

            return null;

        }

        return config.getFixedPrice();

    }



    @Override

    public BigDecimal findSalePriceUsdtByValue(Integer durationMinutes, Integer tierValue) {

        if (durationMinutes == null || durationMinutes <= 0 || tierValue == null || tierValue <= 0) {

            return null;

        }

        LiveTimeDurationConfig duration = durationConfigMapper.selectByDurationValue(durationMinutes);

        LiveUserTierConfig tier = tierConfigMapper.selectByTierValue(tierValue);

        if (duration == null || tier == null) {

            return null;

        }

        return findSalePriceUsdt(duration.getId(), tier.getId());

    }



    private LiveUsdtFixedPriceVo toVo(LiveFixedPriceConfigVo src) {

        LiveUsdtFixedPriceVo vo = new LiveUsdtFixedPriceVo();

        vo.setId(src.getId());

        vo.setDurationId(src.getDurationId());

        vo.setTierId(src.getTierId());

        vo.setDurationName(src.getDurationName());

        vo.setDurationMinutes(src.getDurationValue());

        vo.setPeopleCount(src.getTierValue());

        vo.setStatus(src.getStatus());

        vo.setRemark(src.getRemark());

        vo.setCreateTime(src.getCreateTime());

        vo.setSalePriceUsdt(src.getFixedPrice());

        if (src.getDurationValue() != null && src.getTierValue() != null && src.getFixedPrice() != null) {

            BigDecimal cost = LiveUsdtPriceCalculator.calcCostPriceUsdt(src.getDurationValue(), src.getTierValue());

            vo.setCostPriceUsdt(cost);

            vo.setProfitUsdt(LiveUsdtPriceCalculator.calcProfitUsdt(src.getFixedPrice(), cost));

        }

        return vo;

    }



    private void validateDuration(Long durationId) {

        LiveTimeDurationConfig duration = durationConfigMapper.selectByPrimaryKey(durationId);

        if (duration == null || (duration.getIsDeleted() != null && duration.getIsDeleted() == 1)) {

            throw new IllegalArgumentException("会议时长配置不存在");

        }

    }



    private void validateTier(Long tierId) {

        LiveUserTierConfig tier = tierConfigMapper.selectByPrimaryKey(tierId);

        if (tier == null || (tier.getIsDeleted() != null && tier.getIsDeleted() == 1)) {

            throw new IllegalArgumentException("人数档位配置不存在");

        }

    }



    private void assertNoDuplicate(Long durationId, Long tierId, Long excludeId) {

        if (fixedPriceConfigMapper.countByDurationAndTier(durationId, tierId, LivePricingMode.USDT, excludeId) > 0) {

            throw new IllegalArgumentException("该时长与人数档位组合的 USDT 定价已存在");

        }

    }

    private Long resolveOrCreateDuration(Long durationId, Integer durationMinutes) {

        if (durationId != null) {

            validateDuration(durationId);

            return durationId;

        }

        if (durationMinutes == null || durationMinutes <= 0) {

            throw new IllegalArgumentException("请输入有效的会议时长");

        }

        LiveTimeDurationConfig existing = durationConfigMapper.selectByDurationValue(durationMinutes);

        if (existing != null) {

            return existing.getId();

        }

        LiveTimeDurationConfig created = new LiveTimeDurationConfig();

        created.setDurationName(durationMinutes + "分钟");

        created.setDurationValue(durationMinutes);

        created.setStatus(1);

        if (!configService.createDuration(created) || created.getId() == null) {

            throw new IllegalArgumentException("创建会议时长配置失败");

        }

        return created.getId();

    }

    private Long resolveOrCreateTier(Long tierId, Integer peopleCount) {

        if (tierId != null) {

            validateTier(tierId);

            return tierId;

        }

        if (peopleCount == null || peopleCount <= 0) {

            throw new IllegalArgumentException("请输入有效的会议人数");

        }

        LiveUserTierConfig existing = tierConfigMapper.selectByTierValue(peopleCount);

        if (existing != null) {

            return existing.getId();

        }

        LiveUserTierConfig created = new LiveUserTierConfig();

        created.setTierName(peopleCount + "人");

        created.setTierValue(peopleCount);

        created.setStatus(1);

        if (!configService.createTier(created) || created.getId() == null) {

            throw new IllegalArgumentException("创建人数档位配置失败");

        }

        return created.getId();

    }

}

