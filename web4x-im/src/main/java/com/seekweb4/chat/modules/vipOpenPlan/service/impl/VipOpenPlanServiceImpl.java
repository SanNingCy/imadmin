package com.seekweb4.chat.modules.vipOpenPlan.service.impl;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.vipOpenPlan.dto.VipOpenPlanQueryDto;
import com.seekweb4.chat.modules.vipOpenPlan.entity.VipOpenPlan;
import com.seekweb4.chat.modules.vipOpenPlan.mapper.VipOpenPlanMapper;
import com.seekweb4.chat.modules.vipOpenPlan.service.VipOpenPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class VipOpenPlanServiceImpl implements VipOpenPlanService {

    @Resource
    private VipOpenPlanMapper vipOpenPlanMapper;

    @Override
    public Page<VipOpenPlan> page(VipOpenPlanQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        Page<VipOpenPlan> page = new Page<>(pn, ps);
        queryDto.setPageNo((pn - 1) * ps);
        queryDto.setPageSize(ps);
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(normalizeOrderBy(queryDto.getOrderBy()));
        }
        Long count = vipOpenPlanMapper.selectAdminCount(queryDto);
        page.setCount(count == null ? 0L : count);
        page.setList(vipOpenPlanMapper.selectAdminPageList(queryDto));
        return page;
    }

    @Override
    public VipOpenPlan getById(Long id) {
        return vipOpenPlanMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean save(VipOpenPlan plan) {
        assertNoExistingPlan();
        Date now = new Date();
        plan.setCreateTime(now);
        plan.setUpdateTime(now);
        if (plan.getIsDeleted() == null) {
            plan.setIsDeleted(0);
        }
        if (plan.getStatus() == null) {
            plan.setStatus(1);
        }
        if (plan.getSortOrder() == null) {
            plan.setSortOrder(0);
        }
        return vipOpenPlanMapper.insert(plan) > 0;
    }

    @Override
    public boolean update(VipOpenPlan plan) {
        plan.setUpdateTime(new Date());
        return vipOpenPlanMapper.updateByPrimaryKeySelective(plan) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status, String updateBy) {
        VipOpenPlan plan = new VipOpenPlan();
        plan.setId(id);
        plan.setStatus(status);
        plan.setUpdateBy(updateBy);
        plan.setUpdateTime(new Date());
        return vipOpenPlanMapper.updateByPrimaryKeySelective(plan) > 0;
    }

    @Override
    public boolean remove(Long id, String updateBy) {
        VipOpenPlan plan = new VipOpenPlan();
        plan.setId(id);
        plan.setIsDeleted(1);
        plan.setUpdateBy(updateBy);
        plan.setUpdateTime(new Date());
        return vipOpenPlanMapper.updateByPrimaryKeySelective(plan) > 0;
    }

    @Override
    public List<VipOpenPlan> listEnabled() {
        return vipOpenPlanMapper.selectAllEnabled();
    }

    private void assertNoExistingPlan() {
        Long count = vipOpenPlanMapper.countActive();
        if (count != null && count > 0) {
            throw new IllegalStateException("仅允许配置一个会员开通套餐，请修改现有套餐或先删除后再新建");
        }
    }

    private static String normalizeOrderBy(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        String[] allowed = {"id", "plan_name", "duration_days", "price", "status", "sort_order", "create_time", "update_time"};
        String[] parts = raw.trim().split("\\s+");
        if (parts.length == 0) {
            return null;
        }
        String col = parts[0];
        boolean valid = false;
        for (String a : allowed) {
            if (a.equals(col) || a.equals(camelToSnake(col))) {
                col = a;
                valid = true;
                break;
            }
        }
        if (!valid) {
            return null;
        }
        String direction = "desc";
        if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1])) {
            direction = "asc";
        }
        return col + " " + direction;
    }

    private static String camelToSnake(String name) {
        if (name == null) {
            return null;
        }
        return name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
